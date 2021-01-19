package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.RobberStealCommand

/**
 * @author Vincent76;
 */
abstract class RobberStealState( val nextState:State, controller:Controller ) extends State( controller ) {

  override def robberStealFromPlayer( stealPlayerID:PlayerID ):Unit = controller.action(
    RobberStealCommand( stealPlayerID, this )
  )
}
