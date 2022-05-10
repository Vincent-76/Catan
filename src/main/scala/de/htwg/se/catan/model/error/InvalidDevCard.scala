package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InvalidDevCard extends CustomErrorImpl( "InvalidDevCard" ):
  def fromXML( node:Node ):InvalidDevCard = InvalidDevCard(
    devCard = ( node \ "@devCard" ).content
  )

  def fromJson( json:JsValue ):InvalidDevCard = InvalidDevCard(
    devCard = ( json \ "devCard" ).as[String]
  )


case class InvalidDevCard( devCard:String ) extends CustomError:
  def toXML:Node = <InvalidDevCard devCard={ devCard } />.copy( label = InvalidDevCard.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InvalidDevCard.name ),
    "devCard" -> Json.toJson( devCard )
  )