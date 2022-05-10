package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object NoPlacementPoints extends CustomErrorImpl( "NoPlacementPoints" ):
  def fromXML( node:Node ):NoPlacementPoints = NoPlacementPoints(
    structure = StructurePlacement.of( ( node \ "@structure" ).content ).get
  )

  def fromJson( json:JsValue ):NoPlacementPoints = NoPlacementPoints(
    structure = ( json \ "structure" ).as[StructurePlacement]
  )


case class NoPlacementPoints( structure:StructurePlacement ) extends CustomError:
  def toXML:Node = <NoPlacementPoints structure={ structure.title } />.copy( label = NoPlacementPoints.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( NoPlacementPoints.name ),
    "structure" -> Json.toJson( structure )
  )
