package de.htwg.se.catan.model.info

import de.htwg.se.catan.model.{ Info, InfoImpl }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object GameSavedInfo extends InfoImpl( "GameSavedInfo" ):
  def fromXML( node:Node ):GameSavedInfo = GameSavedInfo(
    path = ( node \ "@path" ).content
  )

  def fromJson( json:JsValue ):GameSavedInfo = GameSavedInfo(
    path = ( json \ "path" ).as[String]
  )


case class GameSavedInfo( path:String ) extends Info:
  def toXML:Node = <GameSavedInfo path={ path } />.copy( label = GameSavedInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( GameSavedInfo.name ),
    "path" -> Json.toJson( path )
  )