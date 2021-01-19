package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.DiceOutBeginnerCommand

/**
 * @author Vincent76;
 */
abstract class InitBeginnerState( val diceValues:Map[PlayerID, Int], val counter:Int, controller:Controller ) extends State( controller ) {

  override def diceOutBeginner( ):Unit = controller.action(
    DiceOutBeginnerCommand( this )
  )
}
