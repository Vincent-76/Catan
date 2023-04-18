package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InvalidPlacementPoint extends CustomErrorImpl( "InvalidPlacementPoint" ):
  def fromXML( node:Node ):InvalidPlacementPoint = InvalidPlacementPoint(
    id = ( node \ "@id" ).content.toInt
  )

  def fromJson( json:JsValue ):InvalidPlacementPoint = InvalidPlacementPoint(
    id = ( json \ "id" ).as[Int]
  )


case class InvalidPlacementPoint( id:Int ) extends CustomError:
  def toXML:Node = <InvalidPlacementPoint id={ id.toString } />.copy( label = InvalidPlacementPoint.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InvalidPlacementPoint.name ),
    "id" -> Json.toJson( id )
  )