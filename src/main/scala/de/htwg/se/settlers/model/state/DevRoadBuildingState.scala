package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.model.{Command, State}
import de.htwg.se.settlers.model.commands.DevBuildRoadCommand

/**
 * @author Vincent76;
 */
case class DevRoadBuildingState( nextState:State, roads:Int = 0 ) extends State {

  override def devBuildRoad( eID:Int ):Option[Command] = Some(
    DevBuildRoadCommand( eID, this )
  )

  //override def toString:String = getClass.getSimpleName + ": NextState[" + nextState + "], Roads[" + roads + "]"
}
