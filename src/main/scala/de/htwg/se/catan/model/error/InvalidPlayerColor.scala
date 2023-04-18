package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InvalidPlayerColor extends CustomErrorImpl( "InvalidPlayerColor" ):
  def fromXML( node:Node ):InvalidPlayerColor = InvalidPlayerColor(
    color = ( node \ "@color" ).content
  )

  def fromJson( json:JsValue ):InvalidPlayerColor = InvalidPlayerColor(
    color = ( json \ "color" ).as[String]
  )


case class InvalidPlayerColor( color:String ) extends CustomError:
  def toXML:Node = <InvalidPlayerColor color={ color } />.copy( label = InvalidPlayerColor.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InvalidPlayerColor.name ),
    "color" -> Json.toJson( color )
  )