package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InvalidPlayerID extends CustomErrorImpl( "InvalidPlayerID" ):
  def fromXML( node:Node ):InvalidPlayerID = InvalidPlayerID(
    id = ( node \ "@id" ).content.toInt
  )

  def fromJson( json:JsValue ):InvalidPlayerID = InvalidPlayerID(
    id = ( json \ "id" ).as[Int]
  )


case class InvalidPlayerID( id:Int ) extends CustomError:
  def toXML:Node = <InvalidPlayerID id={ id.toString } />.copy( label = InvalidPlayerID.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InvalidPlayerID.name ),
    "id" -> Json.toJson( id )
  )