package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object PlayerNameEmptyImpl extends CustomErrorImpl( "PlayerNameEmpty" ):
  def fromXML( node:Node ):CustomError = PlayerNameEmpty

  def fromJson( json:JsValue ):CustomError = PlayerNameEmpty


case object PlayerNameEmpty extends CustomError:
  def toXML:Node = <Fail />.copy( label = PlayerNameEmptyImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( PlayerNameEmptyImpl.name )
  )
