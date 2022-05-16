package de.htwg.se.catan.aview

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpMethod, HttpMethods, HttpRequest, HttpResponse }
import akka.http.scaladsl.unmarshalling.Unmarshal
import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.{ ActionResult, Command, CustomError, DevelopmentCard, Game, Info, PlayerColor, PlayerID, Resource, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.JsonFileIO
import de.htwg.se.catan.util.Observable
import play.api.libs.json.{ Json, Reads, Writes }

import scala.concurrent.{ ExecutionContextExecutor, Future }
import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76
 */
object API:
  val interface:String = sys.env.getOrElse( "CATAN_HOST", "0.0.0.0" )
  val port:Int = sys.env.getOrElse( "CATAN_PORT", 80 ).toString.toInt

class API extends Observable:
  given system:ActorSystem[Nothing] = ActorSystem( Behaviors.empty, "SingleRequest" )
  // needed for the future flatMap/onComplete in the end
  given executionContext:ExecutionContextExecutor = system.executionContext

  def toHttpEntity( data:String ):HttpEntity.Strict = HttpEntity( ContentTypes.`application/json`, data )

  def getURI( path:String ):String = "http://" + API.interface + "/" + path

  def getResult[R]( f:R => Unit, e:Throwable => Unit )( using fjs:Reads[R] ):Try[HttpResponse] => Unit = t => t match
    case Success( response ) => Unmarshal( response.entity ).to[String].onComplete {
      case Success( data ) => try {
        print( data )
        val json = Json.parse( data )
        if response.status.isSuccess() then
          f( json.as[R] )
        else e( json.as[CustomError] )
      } catch {
        case t:Throwable => e( t )
      }
      case Failure( t ) => e( t )
    }

  def rawGet[R]( path:String, f:R => Unit, e:Throwable => Unit )( using fjs:Reads[R] ):Unit = Http().singleRequest( HttpRequest(
    method = HttpMethods.GET,
    uri = getURI( path )
  ) ).onComplete( getResult[R]( f, e ) )

  def execute[R]( f:Future[HttpResponse] )( using fjs:Reads[R] ):Unit =
    f.onComplete( getResult[R](
      res => res match
        case game:Game => update( game, None )
        case result:ActionResult => update( result.game, result.info )
        case _ =>
      , e => error( e )
    ) )

  def get[R]( path:String )( using fjs:Reads[R] ):Unit = execute[R]( Http().singleRequest( HttpRequest(
    method = HttpMethods.GET,
    uri = getURI( path )
  ) ) )

  def cPost[R]( path:String )( using fjs:Reads[R] ):Unit = execute[R]( Http().singleRequest( HttpRequest(
    method = HttpMethods.POST,
    uri = getURI( path )
  ) ) )

  def cPost[E, R]( path:String, entity:E )( using fjs:Writes[E], fjs2:Reads[R] ):Unit = execute[R]( Http().singleRequest( HttpRequest(
    method = HttpMethods.POST,
    uri = getURI( path ),
    entity = toHttpEntity( Json.stringify( Json.toJson( entity ) ) )
  ) ) )

  def post( path:String ):Unit = cPost[ActionResult]( path )

  def post[E]( path:String, entity:E )( using fjs:Writes[E] ):Unit = cPost[E, ActionResult]( path, entity )

  def hasUndo:Boolean = true// Http().singleRequest( HttpRequest( uri = API.interface + "/hasUndo" ) ).
  def hasRedo:Boolean = true

  def undoAction():Unit = post( "undoAction" )
  def redoAction():Unit = post( "redoAction" )

  def saveGame():String = ""//post[String]( "saveGame" )

  def loadGame( path:String ):Unit = post( "loadGame" )

  def exit( info:Option[Info] = None ):Unit = None

  def initGame():Unit = post( "initGame" )
  def addPlayer( playerColor:PlayerColor, name:String ):Unit = post( "addPlayer", Json.obj( "playerColor" -> playerColor, "name" -> name ) )
  def setInitBeginnerState():Unit = post( "setInitBeginnerState" )
  def diceOutBeginner():Unit = post( "diceOutBeginner" )
  def setBeginner():Unit = post( "setBeginner" )
  def buildInitSettlement( vID:Int ):Unit = post( "buildInitSettlement", vID )
  def buildInitRoad( eID:Int ):Unit = post( "buildInitRoad", eID )
  def startTurn():Unit = post( "startTurn" )
  def rollTheDices():Unit = post( "rollTheDices" )
  def useDevCard( devCard:DevelopmentCard ):Unit = post( "useDevCard", devCard )
  def dropResourceCardsToRobber( cards:ResourceCards ):Unit = post( "dropResourceCardsToRobber", cards )
  def placeRobber( hID:Int ):Unit = post( "placeRobber", hID )
  def robberStealFromPlayer( stealPlayerID:PlayerID ):Unit = post( "robberStealFromPlayer", stealPlayerID )
  def setBuildState( structure:StructurePlacement ):Unit = post( "setBuildState", structure )
  def build( id:Int ):Unit = post( "build", id )
  def bankTrade( give:ResourceCards, get:ResourceCards ):Unit = post( "bankTrade", Json.obj( "give" -> give, "get" -> get ) )
  def setPlayerTradeState( give:ResourceCards, get:ResourceCards ):Unit = post( "setPlayerTradeState", Json.obj( "give" -> give, "get" -> get ) )
  def playerTradeDecision( decision:Boolean ):Unit = post( "playerTradeDecision", decision )
  def abortPlayerTrade():Unit = post( "abortPlayerTrade" )
  def playerTrade( tradePlayerID:PlayerID ):Unit = post( "playerTrade", tradePlayerID )
  def buyDevCard():Unit = post( "buyDevCard" )
  def yearOfPlentyAction( resources:ResourceCards ):Unit = post( "yearOfPlentyAction", resources )
  def devBuildRoad( eID:Int ):Unit = post( "devBuildRoad", eID )
  def monopolyAction( r:Resource ):Unit = post( "monopolyAction", r )
  def endTurn():Unit = post( "endTurn" )
