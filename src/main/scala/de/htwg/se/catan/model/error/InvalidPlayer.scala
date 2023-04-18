package de.htwg.se.catan.model.error

import de.htwg.se.catan.model
import de.htwg.se.catan.model.PlayerID
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import de.htwg.se.catan.model.{ CustomError, CustomErrorImpl, StructurePlacement }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object InvalidPlayer extends CustomErrorImpl( "InvalidPlayer" ):
  def fromXML( node:Node ):InvalidPlayer = InvalidPlayer(
    pID = PlayerID.fromXML( node.childOf( "pID" ) )
  )

  def fromJson( json:JsValue ):InvalidPlayer = InvalidPlayer(
    pID = ( json \ "pID" ).as[PlayerID]
  )


case class InvalidPlayer( pID:PlayerID ) extends CustomError:
  def toXML:Node = <InvalidPlayer>
    <pID>{ pID.toXML }</pID>
  </InvalidPlayer>.copy( label = InvalidPlayer.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InvalidPlayer.name ),
    "pID" -> Json.toJson( pID )
  )