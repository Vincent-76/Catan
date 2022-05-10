package de.htwg.se.catan.model.info

import de.htwg.se.catan.model.{ Info, InfoImpl, PlayerID }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76
 */

object GameEndInfo extends InfoImpl( "GameEndInfo" ):
  def fromXML( node:Node ):GameEndInfo = GameEndInfo(
    winner = PlayerID.fromXML( node.childOf( "winner" ) )
  )

  def fromJson( json:JsValue ):GameEndInfo = GameEndInfo(
    winner = ( json \ "winner" ).as[PlayerID]
  )


case class GameEndInfo( winner:PlayerID ) extends Info:
  def toXML:Node = <GameEndInfo>
    <winner>{ winner.toXML }</winner>
  </GameEndInfo>.copy( label = GameEndInfo.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( GameEndInfo.name ),
    "winner" -> Json.toJson( winner )
  )