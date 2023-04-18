package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Card._
import de.htwg.se.catan.model.Card.resourceCardsReads
import de.htwg.se.catan.model.{ Command, PlayerID, Resource, State, StateImpl }
import de.htwg.se.catan.model.commands.PlayerTradeDecisionCommand
import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode, XMLNodeSeq }
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object PlayerTradeState extends StateImpl( "PlayerTradeState" ):
  def fromXML( node:Node ):PlayerTradeState = PlayerTradeState(
    pID = PlayerID.fromXML( node.childOf( "pID" ) ),
    give = ResourceCards.fromXML( node.childOf( "give" ) ),
    get = ResourceCards.fromXML( node.childOf( "get" ) ),
    decisions = node.childOf( "decisions" ).asMap( n => PlayerID.fromXML( n ), _.content.toBoolean )
  )

  def fromJson( json:JsValue ):PlayerTradeState = PlayerTradeState(
    pID = ( json \ "pID" ).as[PlayerID],
    give = ( json \ "give" ).as[ResourceCards],
    get = ( json \ "get" ).as[ResourceCards],
    decisions = ( json \ "decisions" ).asMap[PlayerID, Boolean]
  )


case class PlayerTradeState( pID:PlayerID,
                             give:ResourceCards,
                             get:ResourceCards,
                             decisions:Map[PlayerID, Boolean] ) extends State:

  def toXML:Node = <PlayerTradeState>
    <pID>{ pID.toXML }</pID>
    <give>{ give.toXML( _.title, _.toString ) }</give>
    <get>{ get.toXML( _.title, _.toString ) }</get>
    <decisions>{ decisions.toXML( _.toXML, _.toString ) }</decisions>
  </PlayerTradeState>.copy( label = PlayerTradeState.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( PlayerTradeState.name ),
    "pID" -> Json.toJson( pID ),
    "give" -> Json.toJson( give ),
    "get" -> Json.toJson( get ),
    "decisions" -> Json.toJson( decisions )
  )

  override def playerTradeDecision( decision:Boolean ):Option[Command] = Some(
    PlayerTradeDecisionCommand( decision, this )
  )

  /*override def toString:String = getClass.getSimpleName + ": pID[" + pID + "], give[" + give + "], get[" + get +
    "], Decisions[" + decisions.map( d => d._1 + ": " + d._2 ).mkString( ", " ) + "]"*/
