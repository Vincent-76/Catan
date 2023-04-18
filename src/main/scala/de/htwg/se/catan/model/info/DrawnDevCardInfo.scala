package de.htwg.se.catan.model.info

import de.htwg.se.catan.model.{ DevelopmentCard, Info, InfoImpl, PlayerID }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object DrawnDevCardInfo extends InfoImpl( "DrawnDevCardInfo" ):
  def fromXML( node:Node ):DrawnDevCardInfo = DrawnDevCardInfo(
    pID = PlayerID.fromXML( node.childOf( "pID" ) ),
    devCard = DevelopmentCard.of( node.childOf( "devCard" ).content ).get
  )

  def fromJson( json:JsValue ):DrawnDevCardInfo = DrawnDevCardInfo(
    pID = ( json \ "pID" ).as[PlayerID],
    devCard = ( json \ "devCard" ).as[DevelopmentCard]
  )


case class DrawnDevCardInfo( pID:PlayerID, devCard:DevelopmentCard ) extends Info:
  def toXML:Node = <DrawnDevCardInfo>
    <pID>{ pID.toXML }</pID>
    <devCard>{ devCard.title }</devCard>
  </DrawnDevCardInfo>.copy( label = DrawnDevCardInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( DrawnDevCardInfo.name ),
    "pID" -> Json.toJson( pID ),
    "devCard" -> Json.toJson( devCard )
  )