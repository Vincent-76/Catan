package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object RobberOnlyOnLandImpl extends CustomErrorImpl( "RobberOnlyOnLand" ):
  def fromXML( node:Node ):CustomError = RobberOnlyOnLand

  def fromJson( json:JsValue ):CustomError = RobberOnlyOnLand


case object RobberOnlyOnLand extends CustomError:
  def toXML:Node = <RobberOnlyOnLand />.copy( label = RobberOnlyOnLandImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( RobberOnlyOnLandImpl.name )
  )
