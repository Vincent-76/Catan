package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object NothingToUndoImpl extends CustomErrorImpl( "NothingToUndo" ):
  def fromXML( node:Node ):CustomError = NothingToUndo

  def fromJson( json:JsValue ):CustomError = NothingToUndo


case object NothingToUndo extends CustomError:
  def toXML:Node = <Fail />.copy( label = NothingToUndoImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( NothingToUndoImpl.name )
  )
