package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object AlreadyUsedDevCardInTurnImpl extends CustomErrorImpl( "AlreadyUsedDevCardInTurn" ):
  def fromXML( node:Node ):CustomError = AlreadyUsedDevCardInTurn

  def fromJson( json:JsValue ):CustomError = AlreadyUsedDevCardInTurn


case object AlreadyUsedDevCardInTurn extends CustomError:
  def toXML:Node = <Fail />.copy( label = AlreadyUsedDevCardInTurnImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( AlreadyUsedDevCardInTurnImpl.name )
  )
