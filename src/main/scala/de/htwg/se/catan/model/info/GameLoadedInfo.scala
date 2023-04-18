package de.htwg.se.catan.model.info

import de.htwg.se.catan.model.{ Info, InfoImpl }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object GameLoadedInfo extends InfoImpl( "GameLoadedInfo" ):
  def fromXML( node:Node ):GameLoadedInfo = GameLoadedInfo(
    path = ( node \ "@path" ).content
  )

  def fromJson( json:JsValue ):GameLoadedInfo = GameLoadedInfo(
    path = ( json \ "path" ).as[String]
  )


case class GameLoadedInfo( path:String ) extends Info:
  def toXML:Node = <GameLoadedInfo path={ path } />.copy( label = GameLoadedInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( GameLoadedInfo.name ),
    "path" -> Json.toJson( path )
  )