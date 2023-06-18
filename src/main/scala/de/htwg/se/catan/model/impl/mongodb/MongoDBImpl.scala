package de.htwg.se.catan.model.impl.mongodb

import de.htwg.se.catan.model.{ Command, FileIO, Game }
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import org.mongodb.scala.{ Document, MongoClient, MongoDatabase, Observer, Subscription }
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.result.InsertOneResult
import play.api.libs.json.{ JsObject, JsValue, Json }

import java.util.UUID
import scala.concurrent.{ Await, Future, Promise }
import scala.concurrent.duration.Duration
import scala.reflect.runtime.universe.Try
import scala.util.Success

/**
 * @author Vincent76
 */
object MongoDBImpl extends FileIO( "mongodb" ):
  private final val COLLECTION:String = "ClassicGame"

  private def getConnection:(MongoClient, MongoDatabase) =
    val client = MongoClient( "mongodb://localhost:27017" )
    (client, client.getDatabase( "catan" ))

  override def save( game:Game, undoStack:List[Command], redoStack:List[Command] ):Future[String] =
    val gameID:String = UUID.randomUUID().toString
    val json = Json.obj(
      "id" -> gameID,
      "game" -> Json.toJson( game ),
      "undoStack" -> Json.toJson( undoStack ),
      "redoStack" -> Json.toJson( redoStack )
    )
    val promise = Promise[String]()
    val con = getConnection
    try {
      con._2.getCollection( COLLECTION ).insertOne( Document( Json.stringify( json ) ) ).subscribe( new Observer[InsertOneResult] {
        override def onSubscribe( subscription:Subscription ):Unit = subscription.request( 1 )
        override def onNext( result:InsertOneResult ):Unit = promise.success( gameID )
        override def onError( e:Throwable ):Unit = promise.failure( e )
        override def onComplete():Unit = None
      } )
      promise.future
    } catch {
      case t:Throwable => throw RuntimeException( t )
    } finally {
      con._1.close()
    }

  override def load( gameID:String ):(Game, List[Command], List[Command]) =
    val promise = Promise[Document]()
    val con = getConnection
    try {
      con._2.getCollection( COLLECTION ).find( equal( "id", gameID ) ).subscribe( new Observer[Document] {
        override def onSubscribe( subscription:Subscription ):Unit = subscription.request( 1 )
        override def onNext( result:Document ):Unit = promise.success( result )
        override def onError( e:Throwable ):Unit = promise.failure( e )
        override def onComplete( ):Unit = None
      } )
      val doc = Await.result( promise.future, Duration.Inf )
      val json = Json.parse( doc.toJson() )
      val game = ( json \ "game" ).as[Game]
      val undoStack = ( json \ "undoStack" ).asList[Command]
      val redoStack = ( json \ "redoStack" ).asList[Command]
      (game, undoStack, redoStack)
    } catch {
      case t:Throwable => throw RuntimeException( t )
    } finally {
      con._1.close()
    }