package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object PlacementPointNotEmpty extends CustomErrorImpl( "PlacementPointNotEmpty" ):
  def fromXML( node:Node ):PlacementPointNotEmpty = PlacementPointNotEmpty(
    id = ( node \ "@id" ).content.toInt
  )

  def fromJson( json:JsValue ):PlacementPointNotEmpty = PlacementPointNotEmpty(
    id = ( json \ "id" ).as[Int]
  )


case class PlacementPointNotEmpty( id:Int ) extends CustomError:
  def toXML:Node = <PlacementPointNotEmpty id={ id.toString } />.copy( label = PlacementPointNotEmpty.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( PlacementPointNotEmpty.name ),
    "id" -> Json.toJson( id )
  )