package de.htwg.se.catan.model

import de.htwg.se.catan.model.State.fromJson
import de.htwg.se.catan.model.impl.fileio.{ JsonDeserializer, JsonSerializable, XMLDeserializer, XMLFileIO, XMLSerializable }
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLOption }
import play.api.libs.json.{ JsSuccess, JsValue, Json, Reads }

import scala.xml.Node

/**
 * @author Vincent76
 */

object ActionResult extends XMLDeserializer[ActionResult] with JsonDeserializer[ActionResult]:
  override def fromXML( node:Node ):ActionResult = ActionResult(
    game = Game.fromXML( node.childOf( "game" ) ),
    info = node.childOf( "info" ).asOption( n => Info.fromXML( n ) ),
  )

  override def fromJson( json:JsValue ):ActionResult = ActionResult(
    game = ( json \ "game" ).as[Game],
    info = ( json \ "info" ).asOption[Info]
  )

  given actionResultReads:Reads[ActionResult] = ( json:JsValue ) => JsSuccess( fromJson( json ) )


case class ActionResult( game:Game, info:Option[Info] ) extends XMLSerializable with JsonSerializable:
  override def toXML:Node = <ActionResult>
    <game>{ game.toXML }</game>
    <info>{ info.toXML( _.toXML ) }</info>
  </ActionResult>

  override def toJson:JsValue = Json.obj(
    "game" -> Json.toJson( game ),
    "info" -> Json.toJson( info )
  )
