package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object SettlementRequired extends CustomErrorImpl( "SettlementRequired" ):
  def fromXML( node:Node ):SettlementRequired = SettlementRequired(
    id = ( node \ "@id" ).content.toInt
  )

  def fromJson( json:JsValue ):SettlementRequired = SettlementRequired(
    id = ( json \ "id" ).as[Int]
  )


case class SettlementRequired( id:Int ) extends CustomError:
  def toXML:Node = <SettlementRequired id={ id.toString } />.copy( label = SettlementRequired.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( SettlementRequired.name ),
    "id" -> Json.toJson( id )
  )