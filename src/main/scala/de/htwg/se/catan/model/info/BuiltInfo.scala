package de.htwg.se.catan.model.info

import de.htwg.se.catan.model.{ Info, InfoImpl, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object BuiltInfo extends InfoImpl( "BuiltInfo" ):
  def fromXML( node:Node ):BuiltInfo = BuiltInfo(
    structure = StructurePlacement.of( ( node \ "@structure" ).content ).get,
    id = ( node \ "@id" ).content.toInt
  )

  def fromJson( json:JsValue ):BuiltInfo = BuiltInfo(
    structure = ( json \ "structure" ).as[StructurePlacement],
    id = ( json \ "id" ).as[Int]
  )


case class BuiltInfo( structure:StructurePlacement, id:Int ) extends Info:
  def toXML:Node = <BuiltInfo structure={ structure.title } id={ id.toString } />.copy( label = BuiltInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( BuiltInfo.name ),
    "structure" -> Json.toJson( structure ),
    "id" -> Json.toJson( id )
  )