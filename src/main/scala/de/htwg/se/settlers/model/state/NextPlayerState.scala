package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.ChangeStateCommand

/**
 * @author Vincent76;
 */
case class NextPlayerState( controller:Controller ) extends State( controller ) {

  override def startTurn( ):Unit = controller.action(
    ChangeStateCommand( this, DiceState( controller ) )
  )

  override def toString:String = getClass.getSimpleName
}
