package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, DevelopmentCard, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InsufficientDevCards extends CustomErrorImpl( "InsufficientDevCards" ):
  def fromXML( node:Node ):InsufficientDevCards = InsufficientDevCards(
    devCard = DevelopmentCard.of( ( node \ "@devCard" ).content ).get
  )

  def fromJson( json:JsValue ):InsufficientDevCards = InsufficientDevCards(
    devCard = ( json \ "devCard" ).as[DevelopmentCard]
  )


case class InsufficientDevCards( devCard:DevelopmentCard ) extends CustomError:
  def toXML:Node = <InsufficientDevCards devCard={ devCard.title } />.copy( label = InsufficientDevCards.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InsufficientDevCards.name ),
    "devCard" -> Json.toJson( devCard )
  )