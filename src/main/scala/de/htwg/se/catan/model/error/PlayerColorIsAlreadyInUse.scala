package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.PlayerColor
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNodeSeq
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, DevelopmentCard, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object PlayerColorIsAlreadyInUse extends CustomErrorImpl( "PlayerColorIsAlreadyInUse" ):
  def fromXML( node:Node ):PlayerColorIsAlreadyInUse = PlayerColorIsAlreadyInUse(
    playerColor = PlayerColor.of( ( node \ "@playerColor" ).content ).get
  )

  def fromJson( json:JsValue ):PlayerColorIsAlreadyInUse = PlayerColorIsAlreadyInUse(
    playerColor = ( json \ "playerColor" ).as[PlayerColor]
  )


case class PlayerColorIsAlreadyInUse( playerColor:PlayerColor ) extends CustomError:
  def toXML:Node = <PlayerColorIsAlreadyInUse playerColor={ playerColor.title } />.copy( label = PlayerColorIsAlreadyInUse.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( PlayerColorIsAlreadyInUse.name ),
    "playerColor" -> Json.toJson( playerColor )
  )