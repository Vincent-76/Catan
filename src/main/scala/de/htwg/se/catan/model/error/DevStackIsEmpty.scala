package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object DevStackIsEmptyImpl extends CustomErrorImpl( "DevStackIsEmpty" ):
  def fromXML( node:Node ):CustomError = DevStackIsEmpty

  def fromJson( json:JsValue ):CustomError = DevStackIsEmpty


case object DevStackIsEmpty extends CustomError:
  def toXML:Node = <Fail />.copy( label = DevStackIsEmptyImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( DevStackIsEmptyImpl.name )
  )
