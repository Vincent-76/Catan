package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards._
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.Player.PlayerColor
import de.htwg.se.settlers.model.state.InitState
import de.htwg.se.settlers.util._

import scala.collection.immutable.{ SortedMap, TreeMap, List }
import scala.util.{ Failure, Random, Success, Try }

/**
 * @author Vincent76;
 */
object Game {
  val minPlayers:Int = 3 // ?? As value or method(def)
  val maxPlayers:Int = 4
  val requiredVictoryPoints:Int = 10
  val maxHandCards:Int = 7
  val defaultBankTradeFactor:Int = 4
  val unspecifiedPortFactor:Int = 3
  val specifiedPortFactor:Int = 2
  val maxPlayerNameLength:Int = 10
  val testSeed:Int = 1


  class PlayerID /*private[Game]*/( val id:Int ) {

    override def toString:String = id.toString;
  }

  def apply( test:Boolean ):Game = {
    if ( test ) new Game( gameField = ClassicGameField( new Random( testSeed ) ), seed = testSeed, developmentCards = Cards.getDevStack( new Random( testSeed ) ) ) else Game()
  }

}

object PlayerOrdering extends Ordering[PlayerID] {
  override def compare( x:PlayerID, y:PlayerID ):Int = x.id.compareTo( y.id )
}

case class Game(state:State = InitState(),
                gameField:ClassicGameField = ClassicGameField(),
                resourceStack:ResourceCards = Cards.getResourceCards(),
                developmentCards:List[DevelopmentCard] = Cards.getDevStack(),
                players:SortedMap[PlayerID, Player] = TreeMap.empty[PlayerID, Player]( PlayerOrdering ),
                turn:Turn = Turn( new PlayerID( -1 ) ),
                bonusCards:Map[BonusCard, Option[(PlayerID, Int)]] = Cards.bonusCards.map( (_, Option.empty) ).toMap,
                winner:Option[PlayerID] = Option.empty,
                round:Int = 1,
                seed:Int = Random.nextInt( Int.MaxValue / 1000 )
               ) {

  def copy(state:State = state,
           gameField:ClassicGameField = gameField,
           resourceStack:ResourceCards = resourceStack,
           developmentCards:List[DevelopmentCard] = developmentCards,
           players:SortedMap[PlayerID, Player] = players,
           turn:Turn = turn,
           bonusCards:Map[BonusCard, Option[(PlayerID, Int)]] = bonusCards,
           winner:Option[PlayerID] = winner,
           round:Int = round
          ):Game = new Game( state, gameField, resourceStack, developmentCards, players, turn, bonusCards, winner, round, seed )

  def onTurn:PlayerID = turn.playerID

  def player:Player = player()

  def player( pID:PlayerID = onTurn ):Player = players( pID )

  def isPlayer( playerID:Int ):Boolean = players.keys.exists( _.id == playerID )

  def getPlayerID( playerID:Int ):Option[PlayerID] = players.keys.find( _.id == playerID )

  def nextTurn( pID:PlayerID = onTurn ):PlayerID = players.keys.find( _.id == pID.id + 1 ).getOrElse( players.firstKey )

  def previousTurn( pID:PlayerID = onTurn ):PlayerID = players.keys.find( _.id == pID.id - 1 ).getOrElse( players.lastKey )

  def nextPlayer( p:Player = player ):Player = players( nextTurn( p.id ) )

  def previousPlayer( p:Player = player ):Player = players( previousTurn( p.id ) )

  def setState( state:State ):Game = copy( state = state )

  def addPlayer( playerColor:PlayerColor, name:String ):Game = {
    val pID = new PlayerID( players.size )
    copy( players = players + ( pID -> Player( pID, playerColor, name ) ) )
  }

  def addPlayerF( playerColor:PlayerColor, name:String ):Try[Game] = {
    if( players.exists( _._2.color == playerColor ) )
      Failure( PlayerColorIsAlreadyInUse( playerColor ) )
    else if ( players.exists( _._2.name =^ name ) )
      Failure( PlayerNameAlreadyExists( name ) )
    else Success( addPlayer( playerColor, name ) )
  }

  def removeLastPlayer( ):Game = copy( players = players.init )

  def updatePlayer( player:Player ):Game = copy(
    players = players.updated( player.id, player )
  )

  def updatePlayers( updatePlayers:Player* ):SortedMap[PlayerID, Player] = updatePlayers.red( players,
    ( pl:SortedMap[PlayerID, Player], p:Player ) => pl.updated( p.id, p ) )

  def rollDice( r:Random ):Int = r.nextInt( 6 ) + 1

  def rollDices( ):(Int, Int) = {
    val r = new Random( seed * round )
    (rollDice( r ), rollDice( r ))
  }

  def getAvailableResourceCards( resources:ResourceCards, stack:ResourceCards = resourceStack ):(ResourceCards, ResourceCards) = {
    resources.red( (resources, stack), ( cards:(ResourceCards, ResourceCards), r:Resource, amount:Int ) => {
      val available = cards._2.getOrElse( r, 0 )
      if ( available >= amount )
        (cards._1, cards._2.updated( r, available - amount ))
      else
        (cards._1.updated( r, available ), cards._2.updated( r, 0 ))
    } )
  }

  def drawResourceCards( pID:PlayerID, r:Resource, amount:Int = 1 ):Game =
    drawResourceCards( pID, ResourceCards.ofResource( r, amount ) )

  def drawResourceCards( pID:PlayerID, cards:ResourceCards ):Game = {
    val (available, newStack) = getAvailableResourceCards( cards )
    if ( available.amount > 0 ) {
      val newPlayer = players( pID ).addResourceCards( available )
      copy(
        players = players.updated( newPlayer.id, newPlayer ),
        resourceStack = newStack
      )
    } else this
  }

  def dropResourceCards( pID:PlayerID, r:Resource, amount:Int = 1 ):Try[Game] =
    dropResourceCards( pID, ResourceCards.ofResource( r, amount ) )

  def dropResourceCards( pID:PlayerID, cards:ResourceCards ):Try[Game] = {
    players( pID ).removeResourceCards( cards ) match {
      case Success( nPlayer ) => Success( copy(
        players = players.updated( pID, nPlayer ),
        resourceStack = resourceStack.add( cards )
      ) )
      case f => f.rethrow
    }
  }

  def drawDevCard( pID:PlayerID ):Try[Game] = {
    if ( developmentCards.isEmpty )
      Failure( DevStackIsEmpty )
    else dropResourceCards( pID, Cards.developmentCardCost ) match {
      case Success( newGame ) =>
        val newPlayer = newGame.players( pID ).addDevCard( developmentCards.head )
        Success( newGame.copy(
          players = newGame.players.updated( pID, newPlayer ),
          turn = turn.addDrawnDevCard( developmentCards.head ),
          developmentCards = developmentCards.tail
        ) )
      case Failure( e ) => Failure( e )
    }
  }

  def updateGameField( newField:ClassicGameField ):Game = {
    copy( gameField = newField )
  }

  def getPlayerBonusCards( pID:PlayerID ):Iterable[BonusCard] =
    bonusCards.filter( d => d._2.isDefined && d._2.get._1 == pID ).keys

  def getPlayerDisplayVictoryPoints(pID:PlayerID ):Int =
    getPlayerBonusCards( pID ).red( player( pID ).victoryPoints, ( points:Int, bonusCard:BonusCard ) => points + bonusCard.bonus )

  def getPlayerVictoryPoints(pID:PlayerID ):Int = {
    getPlayerDisplayVictoryPoints( pID ) + player( pID ).devCards.count( _ == GreatHallCard )
  }

  def settlementAmount( pID:PlayerID ):Int = gameField.vertices.count( d => d._2.building.isDefined && d._2.building.get.owner == pID )

  def roadAmount( pID:PlayerID ):Int = gameField.edges.count( d => d._2.road.isDefined && d._2.road.get.owner == pID )

  def noBuildingInRange( v:Vertex ):Boolean = {
    gameField.adjacentEdges( v ).foreach( e1 => {
      gameField.adjacentVertices( e1 ).filter( _ != v ).foreach( v1 => {
        if ( v1.building.nonEmpty )
          return false
      } )
    } )
    true
  }

  def playerHasAdjacentEdge( pID:PlayerID, edges:List[Edge] ):Boolean = {
    edges.foreach( e => {
      if ( e.road.isDefined && e.road.get.owner == pID )
        return true
    } )
    false
  }

  def playerHasAdjacentVertex( pID:PlayerID, vertices:List[Vertex] ):Boolean = {
    vertices.foreach( v => if ( v.building.isDefined && v.building.get.owner == pID )
      return true
    )
    false
  }

  def roadBuildable( edge:Edge, pID:PlayerID ):Boolean = ( playerHasAdjacentEdge( pID, gameField.adjacentEdges( edge ) ) ||
    playerHasAdjacentVertex( pID, gameField.adjacentVertices( edge ) ) ) && ( edge.h1.isLand || edge.h2.isLand )

  private def roadLength( pID:PlayerID, e:Edge, count:Int = 1, previous:List[Edge] = List.empty ):Int = {
    val lengths = gameField.adjacentEdges( e )
      .filter( e => e.road.isDefined && e.road.get.owner == pID && !previous.contains( e ) )
      .map( e2 => roadLength( pID, e2, count + 1, previous :+ e ) )
    if( lengths.isEmpty )
      count
    else lengths.max
  }

  def getRoadLength( pID:PlayerID, e:Edge ):Int = {
    if( e.road.isEmpty || e.road.get.owner != pID )
      0
    else roadLength( pID, e )
  }

  def checkHandCardsInOrder( p:Player = player, dropped:List[PlayerID] = List.empty ):Option[Player] = {
    if ( !dropped.contains( p.id ) && p.resources.amount > Game.maxHandCards )
      Some( p )
    else nextPlayer( p ) match {
      case next:Player if next.id == onTurn => Option.empty
      case next:Player => checkHandCardsInOrder( next, dropped )
    }
  }

  def getBuildableRoadSpotsForSettlement( vID:Int ):List[Edge] = {
    val vertex = gameField.findVertex( vID )
    if( vertex.isDefined && vertex.get.building.isDefined )
      gameField.adjacentEdges( vertex.get ).filter( e => ( e.h1.isLand || e.h2.isLand ) && e.road.isEmpty )
    else List.empty
  }

  def getBankTradeFactor( playerID:PlayerID, r:Resource ):Int = {
    gameField.vertices.values.red( Game.defaultBankTradeFactor, ( factor:Int, v:Vertex ) => {
      if( v.building.isDefined && v.building.get.owner == playerID && v.port.isDefined )
        if( v.port.get.specific.isDefined && v.port.get.specific.get == r )
          Game.specifiedPortFactor
        else if ( v.port.get.specific.isEmpty && Game.unspecifiedPortFactor < factor )
          Game.unspecifiedPortFactor
        else factor
      else factor
    } )
  }

  def getNextTradePlayerInOrder( decisions:Map[PlayerID, Boolean], pID:PlayerID = nextTurn() ):Option[PlayerID] =
    getNextTradePlayerInOrder( decisions, players( pID ) )

  def getNextTradePlayerInOrder( decisions:Map[PlayerID, Boolean], p:Player ):Option[PlayerID] = {
    if( p == player )
      Option.empty
    else if( decisions.contains( p.id ) )
      getNextTradePlayerInOrder( decisions, nextPlayer( p ) )
    else Some( p.id )
  }
}