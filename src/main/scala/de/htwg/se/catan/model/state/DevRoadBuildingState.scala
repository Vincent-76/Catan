package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{Command, State}
import de.htwg.se.catan.model.commands.DevBuildRoadCommand

/**
 * @author Vincent76;
 */
case class DevRoadBuildingState( nextState:State, roads:Int = 0 ) extends State {

  override def devBuildRoad( eID:Int ):Option[Command] = Some(
    DevBuildRoadCommand( eID, this )
  )

  //override def toString:String = getClass.getSimpleName + ": NextState[" + nextState + "], Roads[" + roads + "]"
}
