package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object NothingToRedoImpl extends CustomErrorImpl( "NothingToRedo" ):
  def fromXML( node:Node ):CustomError = NothingToRedo

  def fromJson( json:JsValue ):CustomError = NothingToRedo


case object NothingToRedo extends CustomError:
  def toXML:Node = <Fail />.copy( label = NothingToRedoImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( NothingToRedoImpl.name )
  )
