package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InsufficientResourcesImpl extends CustomErrorImpl( "InsufficientResources" ):
  def fromXML( node:Node ):CustomError = InsufficientResources

  def fromJson( json:JsValue ):CustomError = InsufficientResources


case object InsufficientResources extends CustomError:
  def toXML:Node = <InsufficientResources />.copy( label = InsufficientResourcesImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InsufficientResourcesImpl.name )
  )
