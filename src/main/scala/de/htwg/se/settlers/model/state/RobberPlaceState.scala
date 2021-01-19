package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.PlaceRobberCommand

/**
 * @author Vincent76;
 */
abstract class RobberPlaceState( val nextState:State, controller:Controller ) extends State( controller ) {

  override def placeRobber( hID:Int ):Unit = controller.action(
    PlaceRobberCommand( hID, controller.game.gameField.robber, this )
  )
}
