package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, Resource, State }
import de.htwg.se.catan.model.commands.MonopolyCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode

import scala.xml.Node

/**
 * @author Vincent76;
 */

object MonopolyState {
  def fromXML( node:Node ):MonopolyState = MonopolyState(
    nextState = State.fromXML( node.childOf( "nextState" ) )
  )
}

case class MonopolyState( nextState:State ) extends State {

  def toXML:Node = <MonopolyState>
    <nextState>{ nextState.toXML }</nextState>
  </MonopolyState>

  override def monopolyAction( r:Resource ):Option[Command] = Some(
    MonopolyCommand( r, this )
  )

  //override def toString:String = getClass.getSimpleName + ": nextState[" + nextState + "]"
}
