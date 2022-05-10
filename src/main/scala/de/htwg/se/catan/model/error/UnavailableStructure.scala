package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object UnavailableStructure extends CustomErrorImpl( "UnavailableStructure" ):
  def fromXML( node:Node ):UnavailableStructure = UnavailableStructure(
    structure = StructurePlacement.of( ( node \ "@structure" ).content ).get
  )

  def fromJson( json:JsValue ):UnavailableStructure = UnavailableStructure(
    structure = ( json \ "structure" ).as[StructurePlacement]
  )


case class UnavailableStructure( structure:StructurePlacement ) extends CustomError:
  def toXML:Node = <UnavailableStructure structure={ structure.title } />.copy( label = UnavailableStructure.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( UnavailableStructure.name ),
    "structure" -> Json.toJson( structure )
  )
