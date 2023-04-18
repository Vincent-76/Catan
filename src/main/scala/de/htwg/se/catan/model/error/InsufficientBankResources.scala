package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InsufficientBankResourcesImpl extends CustomErrorImpl( "InsufficientBankResources" ):
  def fromXML( node:Node ):CustomError = InsufficientBankResources

  def fromJson( json:JsValue ):CustomError = InsufficientBankResources


case object InsufficientBankResources extends CustomError:
  def toXML:Node = <Fail />.copy( label = InsufficientBankResourcesImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InsufficientBankResourcesImpl.name )
  )
