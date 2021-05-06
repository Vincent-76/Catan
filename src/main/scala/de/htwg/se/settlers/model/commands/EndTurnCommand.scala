package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.state.NextPlayerState
import de.htwg.se.settlers.model.{ Command, Game, Info, State, Turn }

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class EndTurnCommand( state:State ) extends Command {

  var turn:Option[Turn] = Option.empty

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    turn = Some( game.turn )
    Success( game.copy(
      state = NextPlayerState(),
      turn = Turn( game.nextTurn() ),
      round = game.round + 1
    ), Option.empty )
  }


  override def undoStep( game:Game ):Game = game.copy(
    state = state,
    turn = turn.getOrElse( Turn( game.previousTurn() ) ),
    round = game.round - 1
  )

  //override def toString:String = getClass.getSimpleName + ": " + state + ", turn[" + turn.useOrElse( t => t.playerID, "-" ) + "]"
}
