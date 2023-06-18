package de.htwg.se.catan.aview

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.{ ClientTransport, Http, HttpExt }
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpMethod, HttpMethods, HttpRequest, HttpResponse }
import akka.http.scaladsl.settings.{ ClientConnectionSettings, ConnectionPoolSettings }
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.sslconfig.akka.AkkaSSLConfig
import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.{ ActionResult, Command, CustomError, DevelopmentCard, Game, Info, PlayerColor, PlayerID, Resource, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.JsonFileIO
import de.htwg.se.catan.model.info.GameSavedInfo
import de.htwg.se.catan.util.Observable
import play.api.libs.json.{ Json, Reads, Writes }

import java.net.InetSocketAddress
import scala.concurrent.{ ExecutionContextExecutor, Future }
import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76
 */
object API:
  val interface:String = sys.env.getOrElse( "CATAN_HOST", "0.0.0.0" )
  val port:Int = sys.env.getOrElse( "CATAN_PORT", 8080 ).toString.toInt

class API extends Observable:
  given system:ActorSystem[Nothing] = ActorSystem( Behaviors.empty, "SingleRequest" )
  // needed for the future flatMap/onComplete in the end
  given executionContext:ExecutionContextExecutor = system.executionContext

  var counter:Int = 1

  val http:HttpExt = Http()

  /*val proxy:ClientTransport = ClientTransport.httpsProxy( InetSocketAddress.createUnresolved( "127.0.0.1", 8000 ) )
  val settings = ConnectionPoolSettings( system ).withConnectionSettings( ClientConnectionSettings( system ).withTransport( proxy ) )

  val badSSLConfig = AkkaSSLConfig( system ).mapSettings( s => s.withLoose(
    s.loose
      .withAcceptAnyCertificate( true )
      .withDisableHostnameVerification( true )
  ) )
  val httpsConnectionContext = http.createServerHttpsContext( badSSLConfig )*/

  private def log( action:String, path:String, body:Option[String] = None ):Unit =
    println( s".exec( http( \"request_$counter\" ).$action( \"/$path\" )" + body.map( b =>
      s".body( StringBody( \"\"\"$b\"\"\" ) )"
    ).getOrElse( "" ) + " )" )
    counter = counter + 1

  def toHttpEntity( data:String ):HttpEntity.Strict = HttpEntity( ContentTypes.`application/json`, data )

  private def getURI( path:String ):String = "http://" + API.interface + ":" + API.port + "/" + path

  private def getResult[R]( f:R => Unit, e:Throwable => Unit )( using fjs:Reads[R] ):Try[HttpResponse] => Unit = t => t match
    case Success( response ) => Unmarshal( response.entity ).to[String].onComplete {
      case Success( data ) => try {
        //print( data )
        val json = Json.parse( data )
        if response.status.isSuccess() then
          f( json.as[R] )
        else e( json.as[CustomError] )
      } catch {
        case t:Throwable => e( t )
      }
      case Failure( t ) => e( t )
    }
    case Failure( t ) => e( t )

  private def rawExecute( req:HttpRequest, retries:Int = 3 ):Future[HttpResponse] =
    val future = http.singleRequest( req /*, settings = settings, connectionContext = httpsConnectionContext*/ )
    if retries > 0 then
      future.recoverWith( _ => rawExecute( req, retries - 1 ) )
    else
      future

  def rawGet[R]( path:String, f:R => Unit, e:Throwable => Unit )( using fjs:Reads[R] ):Unit =
    log( "get", path )
    rawExecute( HttpRequest(
      method = HttpMethods.GET,
      uri = getURI( path )
    ) ).onComplete( getResult[R]( f, e ) )
    
  def rawPost( path:String ):Future[HttpResponse] =
    log( "post", path )
    rawExecute( HttpRequest(
      method = HttpMethods.POST,
      uri = getURI( path )
    ) )

  private def execute[R]( req:HttpRequest )( using fjs:Reads[R] ):Unit =
    rawExecute( req ).onComplete( getResult[R](
      res => res match
        case game:Game => update( game, None )
        case result:ActionResult => update( result.game, result.info )
        case i:Info => info( i )
        case _ =>
      , e => error( e )
    ) )

  def get[R]( path:String )( using fjs:Reads[R] ):Unit =
    log( "get", path )
    execute[R]( HttpRequest(
      method = HttpMethods.GET,
      uri = getURI( path )
    ) )

  private def cPost[R]( path:String )( using fjs:Reads[R] ):Unit =
    log( "post", path )
    execute[R]( HttpRequest(
      method = HttpMethods.POST,
      uri = getURI( path )
    ) )

  private def cPost[E, R]( path:String, entity:E )( using fjs:Writes[E], fjs2:Reads[R] ):Unit =
    val entityString = Json.stringify( Json.toJson( entity ) )
    log( "post", path, Some( entityString ) )
    execute[R]( HttpRequest(
      method = HttpMethods.POST,
      uri = getURI( path ),
      entity = toHttpEntity( entityString )
    ) )

  def post( path:String ):Unit = cPost[ActionResult]( path )

  def post[E]( path:String, entity:E )( using fjs:Writes[E] ):Unit = cPost[E, ActionResult]( path, entity )

  def hasUndo:Boolean = true// Http().singleRequest( HttpRequest( uri = API.interface + "/hasUndo" ) ).
  def hasRedo:Boolean = true

  def undoAction():Unit = post( "undoAction" )
  def redoAction():Unit = post( "redoAction" )

  def saveGame():Unit = cPost[GameSavedInfo]( "saveGame" )

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
