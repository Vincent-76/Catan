package de.htwg.se.settlers.model.commands
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Command, Game, Info, State, Turn }

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class EndTurnCommand( state:State ) extends Command {

  var turn:Option[Turn] = Option.empty

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    turn = Some( game.turn )
    Success( game.copy(
      state = controller.ui.getNextPlayerState,
      turn = Turn( game.nextTurn() ),
      round = game.round + 1
    ), Option.empty )
  }


  override def undoStep( game:Game ):Game = game.copy(
    state = state,
    turn = turn.getOrElse( Turn( game.previousTurn() ) ),
    round = game.round - 1
  )
}
