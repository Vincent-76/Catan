package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, State }
import de.htwg.se.catan.model.commands.DevBuildRoadCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object DevRoadBuildingState {
  def fromXML( node:Node ):DevRoadBuildingState = DevRoadBuildingState(
    nextState = State.fromXML( node.childOf( "nextState" ) ),
    roads = ( node \ "@roads" ).content.toInt
  )
}

case class DevRoadBuildingState( nextState:State, roads:Int = 0 ) extends State {

  def toXML:Node = <ActionState roads={ roads.toString }>
    <nextState>{ nextState.toXML }</nextState>
  </ActionState>

  override def devBuildRoad( eID:Int ):Option[Command] = Some(
    DevBuildRoadCommand( eID, this )
  )

  //override def toString:String = getClass.getSimpleName + ": NextState[" + nextState + "], Roads[" + roads + "]"
}
