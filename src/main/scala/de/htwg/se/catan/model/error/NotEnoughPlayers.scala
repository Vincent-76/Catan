package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object NotEnoughPlayersImpl extends CustomErrorImpl( "NotEnoughPlayers" ):
  def fromXML( node:Node ):CustomError = NotEnoughPlayers

  def fromJson( json:JsValue ):CustomError = NotEnoughPlayers


case object NotEnoughPlayers extends CustomError:
  def toXML:Node = <Fail />.copy( label = NotEnoughPlayersImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( NotEnoughPlayersImpl.name )
  )
