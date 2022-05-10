package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object WrongStateImpl extends CustomErrorImpl( "WrongState" ):
  def fromXML( node:Node ):CustomError = WrongState

  def fromJson( json:JsValue ):CustomError = WrongState


case object WrongState extends CustomError:
  def toXML:Node = <WrongState />.copy( label = WrongStateImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( WrongStateImpl.name )
  )
