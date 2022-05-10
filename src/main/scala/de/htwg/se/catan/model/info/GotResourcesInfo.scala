package de.htwg.se.catan.model.info

import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.{ Info, InfoImpl, PlayerID }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLMap }
import de.htwg.se.catan.model.Card.resourceCardsReads
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object GotResourcesInfo extends InfoImpl( "GotResourcesInfo" ):
  def fromXML( node:Node ):GotResourcesInfo = GotResourcesInfo(
    pID = PlayerID.fromXML( node.childOf( "pID" ) ),
    cards = ResourceCards.fromXML( node.childOf( "cards" ) )
  )

  def fromJson( json:JsValue ):GotResourcesInfo = GotResourcesInfo(
    pID = ( json \ "pID" ).as[PlayerID],
    cards = ( json \ "cards" ).as[ResourceCards]
  )


case class GotResourcesInfo( pID:PlayerID, cards:ResourceCards ) extends Info:
  def toXML:Node = <GotResourcesInfo>
    <pID>{ pID.toXML }</pID>
    <cards>{ cards.toXML( _.title, _.toString ) }</cards>
  </GotResourcesInfo>.copy( label = GotResourcesInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( GotResourcesInfo.name ),
    "pID" -> Json.toJson( pID ),
    "cards" -> Json.toJson( cards )
  )