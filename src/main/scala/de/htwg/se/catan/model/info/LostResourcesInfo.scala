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

object LostResourcesInfo extends InfoImpl( "LostResourcesInfo" ):
  def fromXML( node:Node ):LostResourcesInfo = LostResourcesInfo(
    pID = PlayerID.fromXML( node.childOf( "pID" ) ),
    cards = ResourceCards.fromXML( node.childOf( "cards" ) )
  )

  def fromJson( json:JsValue ):LostResourcesInfo = LostResourcesInfo(
    pID = ( json \ "pID" ).as[PlayerID],
    cards = ( json \ "cards" ).as[ResourceCards]
  )


case class LostResourcesInfo( pID:PlayerID, cards:ResourceCards ) extends Info:
  def toXML:Node = <LostResourcesInfo>
    <pID>{ pID.toXML }</pID>
    <cards>{ cards.toXML( _.title, _.toString ) }</cards>
  </LostResourcesInfo>.copy( label = LostResourcesInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( LostResourcesInfo.name ),
    "pID" -> Json.toJson( pID ),
    "cards" -> Json.toJson( cards )
  )