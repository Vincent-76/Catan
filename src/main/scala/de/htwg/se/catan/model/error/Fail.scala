package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object FailImpl extends CustomErrorImpl( "Fail" ):
  def fromXML( node:Node ):CustomError = Fail

  def fromJson( json:JsValue ):CustomError = Fail


case object Fail extends CustomError:
  def toXML:Node = <Fail />.copy( label = FailImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( FailImpl.name )
  )
