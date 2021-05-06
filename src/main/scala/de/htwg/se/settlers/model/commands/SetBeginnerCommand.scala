package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.state.{BuildInitSettlementState, InitBeginnerState}
import de.htwg.se.settlers.model.{Command, Game, Info, NoUniqueBeginner, Turn}

import scala.util.{Failure, Success, Try}

/**
 * @author Vincent76;
 */
case class SetBeginnerCommand( state:InitBeginnerState ) extends Command {

  var oldTurn:Option[Turn] = None

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    if ( state.beginner.isEmpty )
      Failure( NoUniqueBeginner )
    else {
      oldTurn = Some( game.turn )
      Success( game.copy(
        state = BuildInitSettlementState(),
        turn = Turn( state.beginner.get )
      ), None )
    }
  }

  override def undoStep( game:Game ):Game = game.copy(
    state = state,
    turn = oldTurn.getOrElse( game.turn )
  )

  //override def toString:String = getClass.getSimpleName + ": " + state
}
