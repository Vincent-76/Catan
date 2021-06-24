package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, State }
import de.htwg.se.catan.model.commands.PlaceRobberCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode

import scala.xml.Node

/**
 * @author Vincent76;
 */

object RobberPlaceState {
  def fromXML( node:Node ):RobberPlaceState = RobberPlaceState(
    nextState = State.fromXML( node.childOf( "nextState" ) )
  )
}

case class RobberPlaceState( nextState:State ) extends State {

  def toXML:Node = <RobberPlaceState>
    <nextState>{ nextState.toXML }</nextState>
  </RobberPlaceState>

  override def placeRobber( hID:Int ):Option[Command] = Some(
    PlaceRobberCommand( hID, this )
  )

  //override def toString:String = getClass.getSimpleName + ": nextState[" + nextState + "]"
}
