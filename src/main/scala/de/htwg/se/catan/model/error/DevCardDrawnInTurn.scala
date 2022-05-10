package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, DevelopmentCard, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object DevCardDrawnInTurn extends CustomErrorImpl( "DevCardDrawnInTurn" ):
  def fromXML( node:Node ):DevCardDrawnInTurn = DevCardDrawnInTurn(
    devCard = DevelopmentCard.of( ( node \ "@devCard" ).content ).get
  )

  def fromJson( json:JsValue ):DevCardDrawnInTurn = DevCardDrawnInTurn(
    devCard = ( json \ "devCard" ).as[DevelopmentCard]
  )


case class DevCardDrawnInTurn( devCard:DevelopmentCard ) extends CustomError:
  def toXML:Node = <DevCardDrawnInTurn devCard={ devCard.title } />.copy( label = DevCardDrawnInTurn.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( DevCardDrawnInTurn.name ),
    "devCard" -> Json.toJson( devCard )
  )