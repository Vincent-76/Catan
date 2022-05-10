package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InsufficientStructures extends CustomErrorImpl( "InsufficientStructures" ):
  def fromXML( node:Node ):InsufficientStructures = InsufficientStructures(
    structure = StructurePlacement.of( ( node \ "@structure" ).content ).get
  )

  def fromJson( json:JsValue ):InsufficientStructures = InsufficientStructures(
    structure = ( json \ "structure" ).as[StructurePlacement]
  )


case class InsufficientStructures( structure:StructurePlacement ) extends CustomError:
  def toXML:Node = <InsufficientStructures structure={ structure.title } />.copy( label = InsufficientStructures.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InsufficientStructures.name ),
    "structure" -> Json.toJson( structure )
  )
