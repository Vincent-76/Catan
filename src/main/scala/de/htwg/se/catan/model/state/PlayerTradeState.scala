package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.model.{ Command, PlayerID, Resources, State }
import de.htwg.se.catan.model.commands.PlayerTradeDecisionCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode, XMLNodeSeq }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object PlayerTradeState {
  def fromXML( node:Node ):PlayerTradeState = PlayerTradeState(
    pID = PlayerID.fromXML( node.childOf( "pID" ) ),
    give = ResourceCards.fromXML( node.childOf( "give" ) ),
    get = ResourceCards.fromXML( node.childOf( "get" ) ),
    decisions = node.childOf( "decisions" ).convertToMap( n => PlayerID.fromXML( n ), _.content.toBoolean )
  )
}

case class PlayerTradeState( pID:PlayerID,
                             give:ResourceCards,
                             get:ResourceCards,
                             decisions:Map[PlayerID, Boolean] ) extends State {

  def toXML:Node = <PlayerTradeState>
    <pID>{ pID.toXML }</pID>
    <give>{ give.toXML( _.title, _.toString ) }</give>
    <decisions>{ decisions.toXML( _.toXML, _.toString ) }</decisions>
  </PlayerTradeState>

  override def playerTradeDecision( decision:Boolean ):Option[Command] = Some(
    PlayerTradeDecisionCommand( decision, this )
  )

  /*override def toString:String = getClass.getSimpleName + ": pID[" + pID + "], give[" + give + "], get[" + get +
    "], Decisions[" + decisions.map( d => d._1 + ": " + d._2 ).mkString( ", " ) + "]"*/
}
