package de.htwg.se.catan.web

import akka.actor.TypedActor.context
import de.htwg.se.catan.controller.Controller
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{ Route, StandardRoute }
import de.htwg.se.catan.model.*
import de.htwg.se.catan.model.Card.*
import de.htwg.se.catan.model.Card.resourceCardsReads
import de.htwg.se.catan.util.use
import play.api.libs.json.{ JsValue, Json }

import scala.concurrent.Future
import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76
 */
object Requests:
  val interface:String = sys.env.getOrElse( "CATAN_HOST", "0.0.0.0" )
  val port:Int = sys.env.getOrElse( "CATAN_PORT", 8080 ).toString.toInt

class Requests( controller: Controller ):

  def toHttpEntity( data:String ):HttpEntity.Strict = HttpEntity( ContentTypes.`application/json`, data )

  def ok():Route = complete( StatusCodes.OK )

  def ok( s:String ):Route = complete( toHttpEntity( s ) )

  def ok( actionResult:ActionResult ):Route = complete( toHttpEntity( Json.stringify( actionResult.toJson ) ) )

  def ok( game:Game ):Route = complete( toHttpEntity( Json.stringify( game.toJson ) ) )

  def ok( b:Boolean ):Route = complete( toHttpEntity( b.toString ) )

  def fail( e:CustomError ):Route = complete( StatusCodes.BadRequest, toHttpEntity( Json.stringify( e.toJson ) ) )

  def ret( result:Try[ActionResult] ):Route = result match
    case Success( result ) => ok( result )
    case Failure( t ) => t match
      case e:CustomError => fail( e )
      case _ => failWith( t )

  def toJson( f:JsValue => Route ):Route = entity( as[String] ) { data => f( Json.parse( data ) ) }


  given system:ActorSystem[Nothing] = ActorSystem( Behaviors.empty, "system" )
  val bindingFuture:Future[Http.ServerBinding] = Http().newServerAt( Requests.interface, Requests.port ).bind(
    pathPrefix( "game" ) {
      get {
        val jsonString = Json.prettyPrint( Json.toJson( controller.game ) )
        val g = Json.parse( jsonString ).as[Game]
        ok( controller.game )
      }
    } ~
    pathPrefix( "undo" ) {
      get {
        ok( controller.hasUndo )
      } ~
      post {
        ret( controller.undoAction() )
      }
    } ~
    pathPrefix( "redo" ) {
      get {
        ok( controller.hasRedo )
      } ~
      post {
        ret( controller.redoAction() )
      }
    } ~
    pathPrefix( "save" ) {
      post {
        ok( controller.saveGame() )
      }
    } ~
    pathPrefix( "load" ) {
      post {
        entity( as[String] ) { path =>
          ret( controller.loadGame( path ) )
        }
      }
    } ~
    pathPrefix( "exit" ) {
      post {
        controller.exit()
        ok()
      }
    } ~
    pathPrefix( "initGame" ) {
      post {
        ret( controller.initGame() )
      }
    } ~
    pathPrefix( "addPlayer" ) {
      post {
        toJson( json => ret( controller.addPlayer( ( json \ "playerColor" ).as[PlayerColor], ( json \ "name" ).as[String] ) ) )
      }
    } ~
    pathPrefix( "setInitBeginnerState" ) {
      post {
        ret( controller.setInitBeginnerState() )
      }
    } ~
    pathPrefix( "diceOutBeginner" ) {
      post {
        ret( controller.diceOutBeginner() )
      }
    } ~
    pathPrefix( "setBeginner" ) {
      post {
        ret( controller.setBeginner() )
      }
    } ~
    pathPrefix( "buildInitSettlement" ) {
      post {
        toJson( json => ret( controller.buildInitSettlement( json.as[Int] ) ) )
      }
    } ~
    pathPrefix( "buildInitRoad" ) {
      post {
        toJson( json => ret( controller.buildInitRoad( json.as[Int] ) ) )
      }
    } ~
    pathPrefix( "startTurn" ) {
      post {
        ret( controller.startTurn() )
      }
    } ~
    pathPrefix( "rollTheDices" ) {
      post {
        ret( controller.rollTheDices() )
      }
    } ~
    pathPrefix( "useDevCard" ) {
      post {
        toJson( json => ret( controller.useDevCard( json.as[DevelopmentCard] ) ) )
      }
    } ~
    pathPrefix( "dropResourceCardsToRobber" ) {
      post {
        toJson( json => ret( controller.dropResourceCardsToRobber( json.as[ResourceCards] ) ) )
      }
    } ~
    pathPrefix( "placeRobber" ) {
      post {
        toJson( json => ret( controller.placeRobber( json.as[Int] ) ) )
      }
    } ~
    pathPrefix( "robberStealFromPlayer" ) {
      post {
        toJson( json => ret( controller.robberStealFromPlayer( json.as[PlayerID] ) ) )
      }
    } ~
    pathPrefix( "setBuildState" ) {
      post {
        toJson( json => ret( controller.setBuildState( json.as[StructurePlacement] ) ) )
      }
    } ~
    pathPrefix( "build" ) {
      post {
        toJson( json => ret( controller.build( json.as[Int] ) ) )
      }
    } ~
    pathPrefix( "bankTrade" ) {
      post {
        toJson( json => ret( controller.bankTrade( ( json \ "give" ).as[ResourceCards], ( json \ "get" ).as[ResourceCards] ) ) )
      }
    } ~
    pathPrefix( "setPlayerTradeState" ) {
      post {
        toJson( json => ret( controller.setPlayerTradeState( ( json \ "give" ).as[ResourceCards], ( json \ "get" ).as[ResourceCards] ) ) )
      }
    } ~
    pathPrefix( "playerTradeDecision" ) {
      post {
        toJson( json => ret( controller.playerTradeDecision( json.as[Boolean] ) ) )
      }
    } ~
    pathPrefix( "abortPlayerTrade" ) {
      post {
        ret( controller.abortPlayerTrade() )
      }
    } ~
    pathPrefix( "playerTrade" ) {
      post {
        toJson( json => ret( controller.playerTrade( json.as[PlayerID] ) ) )
      }
    } ~
    pathPrefix( "buyDevCard" ) {
      post {
        ret( controller.buyDevCard() )
      }
    } ~
    pathPrefix( "yearOfPlentyAction" ) {
      post {
        toJson( json => ret( controller.yearOfPlentyAction( json.as[ResourceCards] ) ) )
      }
    } ~
    pathPrefix( "devBuildRoad" ) {
      post {
        toJson( json => ret( controller.devBuildRoad( json.as[Int] ) ) )
      }
    } ~
    pathPrefix( "monopolyAction" ) {
      post {
        toJson( json => ret( controller.monopolyAction( json.as[Resource] ) ) )
      }
    } ~
    pathPrefix( "endTurn" ) {
      post {
        ret( controller.endTurn() )
      }
    }
  )

