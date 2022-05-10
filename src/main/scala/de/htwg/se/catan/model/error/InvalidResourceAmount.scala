package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InvalidResourceAmount extends CustomErrorImpl( "InvalidResourceAmount" ):
  def fromXML( node:Node ):InvalidResourceAmount = InvalidResourceAmount(
    amount = ( node \ "@amount" ).content.toInt
  )

  def fromJson( json:JsValue ):InvalidResourceAmount = InvalidResourceAmount(
    amount = ( json \ "amount" ).as[Int]
  )


case class InvalidResourceAmount( amount:Int ) extends CustomError:
  def toXML:Node = <InvalidResourceAmount amount={ amount.toString } />.copy( label = InvalidResourceAmount.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InvalidResourceAmount.name ),
    "amount" -> Json.toJson( amount )
  )