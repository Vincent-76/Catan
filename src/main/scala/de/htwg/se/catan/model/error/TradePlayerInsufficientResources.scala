package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object TradePlayerInsufficientResourcesImpl extends CustomErrorImpl( "TradePlayerInsufficientResources" ):
  def fromXML( node:Node ):CustomError = TradePlayerInsufficientResources

  def fromJson( json:JsValue ):CustomError = TradePlayerInsufficientResources


case object TradePlayerInsufficientResources extends CustomError:
  def toXML:Node = <TradePlayerInsufficientResources />.copy( label = TradePlayerInsufficientResourcesImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( TradePlayerInsufficientResourcesImpl.name )
  )
