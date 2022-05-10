package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object PlayerNameTooLong extends CustomErrorImpl( "PlayerNameTooLong" ):
  def fromXML( node:Node ):PlayerNameTooLong = PlayerNameTooLong(
    name = ( node \ "@name" ).content
  )

  def fromJson( json:JsValue ):PlayerNameTooLong = PlayerNameTooLong(
    name = ( json \ "name" ).as[String]
  )


case class PlayerNameTooLong( name:String ) extends CustomError:
  def toXML:Node = <PlayerNameTooLong name={ name } />.copy( label = PlayerNameTooLong.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( PlayerNameTooLong.name ),
    "name" -> Json.toJson( name )
  )