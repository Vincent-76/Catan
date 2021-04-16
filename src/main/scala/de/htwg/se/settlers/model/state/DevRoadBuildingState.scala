package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.DevBuildRoadCommand

/**
 * @author Vincent76;
 */
case class DevRoadBuildingState( controller:Controller, nextState:State, roads:Int = 0 ) extends State( controller ) {

  override def devBuildRoad( eID:Int ):Unit = controller.action(
    DevBuildRoadCommand( eID, this )
  )

  override def toString:String = getClass.getSimpleName + ": NextState[" + nextState + "], Roads[" + roads + "]"
}
