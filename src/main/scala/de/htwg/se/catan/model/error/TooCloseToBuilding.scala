package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object TooCloseToBuilding extends CustomErrorImpl( "TooCloseToBuilding" ):
  def fromXML( node:Node ):TooCloseToBuilding = TooCloseToBuilding(
    id = ( node \ "@id" ).content.toInt
  )

  def fromJson( json:JsValue ):TooCloseToBuilding = TooCloseToBuilding(
    id = ( json \ "id" ).as[Int]
  )


case class TooCloseToBuilding( id:Int ) extends CustomError:
  def toXML:Node = <TooCloseToBuilding id={ id.toString } />.copy( label = TooCloseToBuilding.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( TooCloseToBuilding.name ),
    "id" -> Json.toJson( id )
  )