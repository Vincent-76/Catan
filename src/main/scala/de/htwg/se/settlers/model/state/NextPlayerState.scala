package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.model.{Command, State}
import de.htwg.se.settlers.model.commands.ChangeStateCommand

/**
 * @author Vincent76;
 */
case class NextPlayerState() extends State {

  override def startTurn( ):Option[Command] = Some(
    ChangeStateCommand( this, DiceState() )
  )

  //override def toString:String = getClass.getSimpleName
}
