package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.StartTurnCommand

/**
 * @author Vincent76;
 */
abstract class NextPlayerState( controller:Controller ) extends State( controller ) {

  override def startTurn( ):Unit = controller.action(
    StartTurnCommand( this )
  )


}
