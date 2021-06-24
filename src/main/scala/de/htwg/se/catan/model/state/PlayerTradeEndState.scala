package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.model.{ Command, PlayerID, Resources, State }
import de.htwg.se.catan.model.commands.{ AbortPlayerTradeCommand, PlayerTradeCommand }
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode, XMLNodeSeq }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object PlayerTradeEndState {
  def fromXML( node:Node ):PlayerTradeEndState = PlayerTradeEndState(
    give = ResourceCards.fromXML( node.childOf( "give" ) ),
    get = ResourceCards.fromXML( node.childOf( "get" ) ),
    decisions = node.childOf( "decisions" ).convertToMap( n => PlayerID.fromXML( n ), _.content.toBoolean )
  )
}

case class PlayerTradeEndState( give:ResourceCards,
                                get:ResourceCards,
                                decisions:Map[PlayerID, Boolean] ) extends State {

  def toXML:Node = <PlayerTradeEndState>
    <give>{ give.toXML( _.title, _.toString ) }</give>
    <get>{ get.toXML( _.title, _.toString ) }</get>
    <decisions>{ decisions.toXML( _.toXML, _.toString ) }</decisions>
  </PlayerTradeEndState>

  override def playerTrade( tradePlayerID:PlayerID ):Option[Command] = Some(
    PlayerTradeCommand( tradePlayerID, this )
  )

  override def abortPlayerTrade( ):Option[Command] = Some(
    AbortPlayerTradeCommand( this )
  )

  /*override def toString:String = getClass.getSimpleName + ": Give[" + give + "], Get[" + get +
    "], Decisions[" + decisions.map( d => d._1.id + ": " + d._2 ).mkString( ", " ) + "]"*/
}
