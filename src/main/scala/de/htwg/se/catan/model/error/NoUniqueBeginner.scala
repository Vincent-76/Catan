package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object NoUniqueBeginnerImpl extends CustomErrorImpl( "NoUniqueBeginner" ):
  def fromXML( node:Node ):CustomError = NoUniqueBeginner

  def fromJson( json:JsValue ):CustomError = NoUniqueBeginner


case object NoUniqueBeginner extends CustomError:
  def toXML:Node = <NoUniqueBeginner />.copy( label = NoUniqueBeginnerImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( NoUniqueBeginnerImpl.name )
  )
