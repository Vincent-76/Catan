package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Command, Game, Info }
import de.htwg.se.settlers.model.state.{ DiceState, NextPlayerState }

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class StartTurnCommand( state:NextPlayerState ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = Success( game.copy(
    state = DiceState( controller )
  ), Option.empty )

  override def undoStep( game:Game ):Game = game.copy(
    state = state
  )
}
