package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InconsistentDataImpl extends CustomErrorImpl( "InconsistentData" ):
  def fromXML( node:Node ):CustomError = InconsistentData

  def fromJson( json:JsValue ):CustomError = InconsistentData


case object InconsistentData extends CustomError:
  def toXML:Node = <Fail />.copy( label = InconsistentDataImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InconsistentDataImpl.name )
  )
