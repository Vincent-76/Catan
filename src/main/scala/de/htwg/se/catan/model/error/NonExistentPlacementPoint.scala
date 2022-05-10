package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object NonExistentPlacementPoint extends CustomErrorImpl( "NonExistentPlacementPoint" ):
  def fromXML( node:Node ):NonExistentPlacementPoint = NonExistentPlacementPoint(
    id = ( node \ "@id" ).content.toInt
  )

  def fromJson( json:JsValue ):NonExistentPlacementPoint = NonExistentPlacementPoint(
    id = ( json \ "id" ).as[Int]
  )


case class NonExistentPlacementPoint( id:Int ) extends CustomError:
  def toXML:Node = <InsufficientStructures id={ id.toString } />.copy( label = NonExistentPlacementPoint.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( NonExistentPlacementPoint.name ),
    "id" -> Json.toJson( id )
  )