package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.{ DiceOutBeginnerCommand, SetBeginnerCommand }

/**
 * @author Vincent76;
 */
case class InitBeginnerState( controller:Controller,
                              beginner:Option[PlayerID] = Option.empty,
                              diceValues:Map[PlayerID, Int] = Map.empty,
                              counter:Int = 1 ) extends State( controller ) {

  override def diceOutBeginner( ):Unit = controller.action(
    DiceOutBeginnerCommand( this )
  )

  override def setBeginner( ):Unit = controller.action(
    SetBeginnerCommand( this )
  )
}
