package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.PlaceRobberCommand

/**
 * @author Vincent76;
 */
case class RobberPlaceState( controller:Controller,
                             nextState:State ) extends State( controller ) {

  override def placeRobber( hID:Int ):Unit = controller.action(
    PlaceRobberCommand( hID, controller.game.gameField.robber, this )
  )

  override def toString:String = getClass.getSimpleName + ": nextState[" + nextState + "]"
}
