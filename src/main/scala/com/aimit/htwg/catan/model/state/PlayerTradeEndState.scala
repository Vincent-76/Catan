package com.aimit.htwg.catan.model.state

import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.model.commands.{ AbortPlayerTradeCommand, PlayerTradeCommand }
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode, XMLNodeSeq }
import com.aimit.htwg.catan.model.{ Command, PlayerID, State, StateImpl }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object PlayerTradeEndState extends StateImpl( "PlayerTradeEndState" ) {
  def fromXML( node:Node ):PlayerTradeEndState = PlayerTradeEndState(
    give = ResourceCards.fromXML( node.childOf( "give" ) ),
    get = ResourceCards.fromXML( node.childOf( "get" ) ),
    decisions = node.childOf( "decisions" ).asMap( n => PlayerID.fromXML( n ), _.content.toBoolean )
  )

  def fromJson( json:JsValue ):PlayerTradeEndState = PlayerTradeEndState(
    give = ( json \ "give" ).as[ResourceCards],
    get = ( json \ "get" ).as[ResourceCards],
    decisions = ( json \ "decisions" ).asMap[PlayerID, Boolean]
  )
}

case class PlayerTradeEndState( give:ResourceCards,
                                get:ResourceCards,
                                decisions:Map[PlayerID, Boolean] ) extends State {

  def toXML:Node = <PlayerTradeEndState>
    <give>{ give.toXML( _.name, _.toString ) }</give>
    <get>{ get.toXML( _.name, _.toString ) }</get>
    <decisions>{ decisions.toXML( _.toXML, _.toString ) }</decisions>
  </PlayerTradeEndState>.copy( label = PlayerTradeEndState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( PlayerTradeEndState.name ),
    "give" -> Json.toJson( give ),
    "get" -> Json.toJson( get ),
    "decisions" -> Json.toJson( decisions )
  )

  override def playerTrade( tradePlayerID:PlayerID ):Option[Command] = Some(
    PlayerTradeCommand( tradePlayerID, this )
  )

  override def abortPlayerTrade( ):Option[Command] = Some(
    AbortPlayerTradeCommand( this )
  )

  /*override def toString:String = getClass.getSimpleName + ": Give[" + give + "], Get[" + get +
    "], Decisions[" + decisions.map( d => d._1.id + ": " + d._2 ).mkString( ", " ) + "]"*/
}
