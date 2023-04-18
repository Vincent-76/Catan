package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object NoAdjacentStructureImpl extends CustomErrorImpl( "NoAdjacentStructure" ):
  def fromXML( node:Node ):CustomError = NoAdjacentStructure

  def fromJson( json:JsValue ):CustomError = NoAdjacentStructure


case object NoAdjacentStructure extends CustomError:
  def toXML:Node = <NoAdjacentStructure />.copy( label = NoAdjacentStructureImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( NoAdjacentStructureImpl.name )
  )
