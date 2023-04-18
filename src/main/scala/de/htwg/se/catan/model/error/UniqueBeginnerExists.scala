package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object UniqueBeginnerExistsImpl extends CustomErrorImpl( "UniqueBeginnerExists" ):
  def fromXML( node:Node ):CustomError = UniqueBeginnerExists

  def fromJson( json:JsValue ):CustomError = UniqueBeginnerExists


case object UniqueBeginnerExists extends CustomError:
  def toXML:Node = <UniqueBeginnerExists />.copy( label = UniqueBeginnerExistsImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( UniqueBeginnerExistsImpl.name )
  )
