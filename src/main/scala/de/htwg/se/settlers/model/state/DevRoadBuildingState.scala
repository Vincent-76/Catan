package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.DevBuildRoadCommand

/**
 * @author Vincent76;
 */
abstract class DevRoadBuildingState( val nextState:State,
                                     val roads:Int = 0,
                                     controller:Controller ) extends State( controller ) {

  override def devBuildRoad( eID:Int ):Unit = controller.action(
    DevBuildRoadCommand( eID, this )
  )
}
