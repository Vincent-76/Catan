package de.htwg.se.catan.model.info

import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.{ Info, InfoImpl, PlayerID }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.*
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.Card.resourceCardsReads
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object GatherInfo extends InfoImpl( "GatherInfo" ):
  def fromXML( node:Node ):GatherInfo = GatherInfo(
    dices = node.childOf( "dices" ).asTuple( _.content.toInt, _.content.toInt ),
    playerResources = node.childOf( "playerResources" ).asMap( n => PlayerID.fromXML( n ), n => ResourceCards.fromXML( n ) )
  )

  def fromJson( json:JsValue ):GatherInfo = GatherInfo(
    dices = ( json \ "dices" ).asTuple[Int, Int],
    playerResources = ( json \ "playerResources" ).asMap[PlayerID, ResourceCards]
  )


case class GatherInfo( dices:(Int, Int), playerResources:Map[PlayerID, ResourceCards] ) extends Info:
  def toXML:Node = <GatherInfo>
    <dices>{ dices.toXML }</dices>
    <playerResources>{ playerResources.toXML( _.toXML, _.toXML( _.title, _.toString ) ) }</playerResources>
  </GatherInfo>.copy( label = GatherInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( GatherInfo.name ),
    "dices" -> Json.toJson( dices ),
    "playerResources" -> Json.toJson( playerResources )
  )