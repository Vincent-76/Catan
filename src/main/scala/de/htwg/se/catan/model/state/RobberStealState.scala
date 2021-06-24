package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, PlayerID, State }
import de.htwg.se.catan.model.commands.RobberStealCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLSequence }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object RobberStealState {
  def fromXML( node:Node ):RobberStealState = RobberStealState(
    adjacentPlayers = node.childOf( "adjacentPlayers" ).convertToList( n => PlayerID.fromXML( n ) ),
    nextState = State.fromXML( node.childOf( "nextState" ) )
  )
}

case class RobberStealState( adjacentPlayers:List[PlayerID],
                             nextState:State ) extends State {

  def toXML:Node = <RobberStealState>
    <adjacentPlayers>{ adjacentPlayers.toXML( _.toXML ) }</adjacentPlayers>
    <nextState>{ nextState.toXML }</nextState>
  </RobberStealState>

  override def robberStealFromPlayer( stealPlayerID:PlayerID ):Option[Command] = Some(
    RobberStealCommand( stealPlayerID, this )
  )

  /*override def toString:String = getClass.getSimpleName + ": adjacentPlayers[" + adjacentPlayers.mkString( ", " ) +
    "], NextState[" + nextState + "]"*/
}
