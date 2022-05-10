package de.htwg.se.catan.model.info

import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.impl.fileio.XMLFileIO
import de.htwg.se.catan.model.{ Info, InfoImpl, PlayerID }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode, XMLNodeSeq }
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.Card.resourceCardsReads
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object ResourceChangeInfo extends InfoImpl( "ResourceChangeInfo" ):
  def fromXML( node:Node ):ResourceChangeInfo = ResourceChangeInfo(
    playerAdd = node.childOf( "playerAdd" ).asMap( n => PlayerID.fromXML( n ), n => ResourceCards.fromXML( n ) ),
    playerSub = node.childOf( "playerSub" ).asMap( n => PlayerID.fromXML( n ), n => ResourceCards.fromXML( n ) )
  )

  def fromJson( json:JsValue ):ResourceChangeInfo = ResourceChangeInfo(
    playerAdd = ( json \ "playerAdd" ).asMap[PlayerID, ResourceCards],
    playerSub = ( json \ "playerSub" ).asMap[PlayerID, ResourceCards],
  )


case class ResourceChangeInfo( playerAdd:Map[PlayerID, ResourceCards], playerSub:Map[PlayerID, ResourceCards] ) extends Info:
  def toXML:Node = <ResourceChangeInfo>
    <playerAdd>{ playerAdd.toXML( _.toXML, _.toXML( _.title, _.toString ) ) }</playerAdd>
    <playerSub>{ playerSub.toXML( _.toXML, _.toXML( _.title, _.toString ) ) }</playerSub>
  </ResourceChangeInfo>.copy( label = ResourceChangeInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( ResourceChangeInfo.name ),
    "playerAdd" -> Json.toJson( playerAdd ),
    "playerSub" -> Json.toJson( playerSub )
  )