package de.htwg.se.catan.model.impl.slick

import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.{ BonusCard, Command, DevelopmentCard, FileIO, Game, GameField, Placement, Player, PlayerID, PlayerOrdering, State, Turn }
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonValue
import de.htwg.se.catan.model.Card.resourceCardsReads
import de.htwg.se.catan.util.*
import play.api.libs.json.Json
import slick.dbio.{ DBIO, DatabaseAction }
import slick.jdbc.JdbcBackend
import slick.lifted.{ Query, TableQuery }
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api.*

import scala.collection.immutable.TreeMap
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration
import concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76
 */

object SlickImpl extends FileIO( "slick" ):
  private def getConnection:Database = Database.forURL( "jdbc:mysql://localhost:3306/test?useSSL=false", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "" )

  override def save( game:Game, undoStack:List[Command], redoStack:List[Command] ):Future[String] = game match
    case g:ClassicGameImpl => saveClassicGame( g, undoStack, redoStack )
    case _ => Future.failed( NotImplementedError( game.getClass.toString ) )

  def saveClassicGame( game:ClassicGameImpl, undoStack:List[Command], redoStack:List[Command] ):Future[String] = Try {
    val db = getConnection
    val gameQuery:TableQuery[SlickClassicGame] = TableQuery[SlickClassicGame]
    val commandQuery:TableQuery[SlickCommand] = TableQuery[SlickCommand]


    db.run( ( gameQuery.schema ++ commandQuery.schema ).createIfNotExists )
      .flatMap( _ => db.run( gameQuery.returning( gameQuery.map( _.id ) ) += (
        0,
        Json.stringify( game.gameFieldVal.toJson ),
        Json.stringify( game.turnVal.toJson ),
        game.seed,
        game.playerFactoryClass,
        Json.stringify( Json.toJson( game.availablePlacementsVal ) ),
        Json.stringify( game.state.toJson ),
        Json.stringify( Json.toJson( game.resourceStack ) ),
        Json.stringify( Json.toJson( game.developmentCards ) ),
        Json.stringify( Json.toJson( game.players ) ),
        Json.stringify( Json.toJson( game.bonusCards ) ),
        Json.stringify( Json.toJson( game.winner ) ),
        game.round
      ) ) )
      .flatMap( gameID => db.run( DBIO.seq(
          commandQuery ++= undoStack.zipWithIndex.map( d => (0, gameID, true, d._2, Json.stringify( d._1.toJson )) ),
          commandQuery ++= redoStack.zipWithIndex.map( d => (0, gameID, false, d._2, Json.stringify( d._1.toJson )) )
      ) ).map( _ => gameID.toString ) )
      .andThen( gameID => {
        db.close()
        gameID
      } )
  } match
    case Success( f ) => f
    case Failure( t ) => Future.failed( t )

  override def load( id:String ):(Game, List[Command], List[Command]) =
    try {
      val db = getConnection
      try {
        implicit val session:JdbcBackend.Session = db.createSession()
        val gameID = id.toInt
        val query = sql"""SELECT * FROM ClassicGame WHERE id = $gameID""".as[(Int, String, String, Int, String, String, String, String, String, String, String, String, Int)]
        val gameData = Await.result( db.run( query ), Duration.Inf ).head
        val commandQuery = sql"""SELECT * FROM Command WHERE gameID = $gameID""".as[(Int, Int, Boolean, Int, String)]
        val commandData = Await.result( db.run( commandQuery ), Duration.Inf )

        /*val gameQuery = TableQuery[SlickClassicGame]
        val query = gameQuery.filter( _.id === gameID ).take( 1 )
        val r = query.result
        val f = db.run( r )
        val gameDatas = Await.result( f, Duration.Inf )
        val gameData = gameDatas.head
        print( gameData._1 )
        val cQuery = TableQuery[SlickCommand].filter( _.gameID == gameID )
        val commandData = Await.result( db.run( cQuery.result ), Duration.Inf )*/

        val (undoList, redoList) = commandData.sortBy( - _._4 ).red( (List.empty[Command], List.empty[Command]), ( l:(List[Command], List[Command]), d:(Int, Int, Boolean, Int, String) ) => {
          val command = Command.fromJson( Json.parse( d._5 ) )
          if d._3 then (command :: l._1, l._2)
          else (l._1, command :: l._2)
        } )

        (ClassicGameImpl(
          gameFieldVal = Json.parse( gameData._2 ).as[GameField],
          turnVal = Json.parse( gameData._3 ).as[Turn],
          seedVal = gameData._4,
          playerFactory = CatanModule.playerFactoryFromString( gameData._5 ).get,
          playerFactoryClass = gameData._5,
          availablePlacementsVal = Json.parse( gameData._6 ).asList[Placement],
          stateVal = Json.parse( gameData._7 ).as[State],
          resourceStack = Json.parse( gameData._8 ).as[ResourceCards],
          developmentCards = Json.parse( gameData._9 ).asList[DevelopmentCard],
          playersVal = TreeMap( Json.parse( gameData._10 ).asMap[PlayerID, Player].toIndexedSeq:_* )( PlayerOrdering ),
          bonusCardsVal = Json.parse( gameData._11 ).asMapC( _.as[BonusCard], _.asOptionC( _.asTuple[PlayerID, Int] ) ),
          winnerVal = Json.parse( gameData._12 ).asOption[PlayerID],
          roundVal = gameData._13
        ), undoList, redoList)
      } catch {
        case e:Throwable => throw e
      } finally {
        db.close()
      }
    } catch {
      case e:Exception => throw RuntimeException( e )
    }