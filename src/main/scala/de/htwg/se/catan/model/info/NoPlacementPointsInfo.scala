package de.htwg.se.catan.model.info

import de.htwg.se.catan.model.{ Info, InfoImpl, PlayerID, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object NoPlacementPointsInfo extends InfoImpl( "NoPlacementPointsInfo" ):
  def fromXML( node:Node ):NoPlacementPointsInfo = NoPlacementPointsInfo(
    pID = PlayerID.fromXML( node.childOf( "pID" ) ),
    structure = StructurePlacement.of( node.childOf( "structure" ).content ).get
  )

  def fromJson( json:JsValue ):NoPlacementPointsInfo = NoPlacementPointsInfo(
    pID = ( json \ "pID" ).as[PlayerID],
    structure = ( json \ "structure" ).as[StructurePlacement]
  )


case class NoPlacementPointsInfo( pID:PlayerID, structure:StructurePlacement ) extends Info:
  def toXML:Node = <NoPlacementPointsInfo>
    <pID>{ pID.toXML }</pID>
    <structure>{ structure.title }</structure>
  </NoPlacementPointsInfo>.copy( label = NoPlacementPointsInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( NoPlacementPointsInfo.name ),
    "pID" -> Json.toJson( pID ),
    "structure" -> Json.toJson( structure )
  )