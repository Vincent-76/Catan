package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object PlayerNameAlreadyExists extends CustomErrorImpl( "PlayerNameAlreadyExists" ):
  def fromXML( node:Node ):PlayerNameAlreadyExists = PlayerNameAlreadyExists(
    name = ( node \ "@name" ).content
  )

  def fromJson( json:JsValue ):PlayerNameAlreadyExists = PlayerNameAlreadyExists(
    name = ( json \ "name" ).as[String]
  )


case class PlayerNameAlreadyExists( name:String ) extends CustomError:
  def toXML:Node = <PlayerNameAlreadyExists name={ name } />.copy( label = PlayerNameAlreadyExists.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( PlayerNameAlreadyExists.name ),
    "name" -> Json.toJson( name )
  )