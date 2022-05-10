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

object BankTradedInfo extends InfoImpl( "BankTradedInfo" ):
  def fromXML( node:Node ):BankTradedInfo = BankTradedInfo(
    pID = PlayerID.fromXML( node.childOf( "pID" ) ),
    give = ResourceCards.fromXML( node.childOf( "give" ) ),
    get = ResourceCards.fromXML( node.childOf( "get" ) ),
  )

  def fromJson( json:JsValue ):BankTradedInfo = BankTradedInfo(
    pID = ( json \ "pID" ).as[PlayerID],
    give = ( json \ "give" ).as[ResourceCards],
    get = ( json \ "get" ).as[ResourceCards]
  )


case class BankTradedInfo( pID:PlayerID, give:ResourceCards, get:ResourceCards ) extends Info:
  def toXML:Node = <BankTradedInfo>
    <pID>{ pID.toXML }</pID>
    <give>{ give.toXML( _.title, _.toString ) }</give>
    <get>{ get.toXML( _.title, _.toString ) }</get>
  </BankTradedInfo>.copy( label = BankTradedInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( BankTradedInfo.name ),
    "pID" -> Json.toJson( pID ),
    "give" -> Json.toJson( give ),
    "get" -> Json.toJson( get ),
  )