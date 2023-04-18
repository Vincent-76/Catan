package de.htwg.se.catan.model.info

import de.htwg.se.catan.model.{ Info, InfoImpl, PlayerID, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InsufficientStructuresInfo extends InfoImpl( "InsufficientStructuresInfo" ):
  def fromXML( node:Node ):InsufficientStructuresInfo = InsufficientStructuresInfo(
    pID = PlayerID.fromXML( node.childOf( "pID" ) ),
    structure = StructurePlacement.of( node.childOf( "structure" ).content ).get
  )

  def fromJson( json:JsValue ):InsufficientStructuresInfo = InsufficientStructuresInfo(
    pID = ( json \ "pID" ).as[PlayerID],
    structure = ( json \ "structure" ).as[StructurePlacement]
  )


case class InsufficientStructuresInfo( pID:PlayerID, structure:StructurePlacement ) extends Info:
  def toXML:Node = <InsufficientStructuresInfo>
    <pID>{ pID.toXML }</pID>
    <structure>{ structure.title }</structure>
  </InsufficientStructuresInfo>.copy( label = InsufficientStructuresInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InsufficientStructuresInfo.name ),
    "pID" -> Json.toJson( pID ),
    "structure" -> Json.toJson( structure )
  )