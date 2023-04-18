package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object NoConnectedStructures extends CustomErrorImpl( "NoConnectedStructures" ):
  def fromXML( node:Node ):NoConnectedStructures = NoConnectedStructures(
    id = ( node \ "@id" ).content.toInt
  )

  def fromJson( json:JsValue ):NoConnectedStructures = NoConnectedStructures(
    id = ( json \ "id" ).as[Int]
  )


case class NoConnectedStructures( id:Int ) extends CustomError:
  def toXML:Node = <NoConnectedStructures id={ id.toString } />.copy( label = NoConnectedStructures.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( NoConnectedStructures.name ),
    "id" -> Json.toJson( id )
  )