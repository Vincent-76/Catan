package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.state.{ BuildInitSettlementState, InitBeginnerState }
import de.htwg.se.settlers.model.{ Command, Game, Info, NoUniqueBeginner, Turn }

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class SetBeginnerCommand( state:InitBeginnerState ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    if ( state.beginner.isEmpty )
      Failure( NoUniqueBeginner )
    else
      Success( game.copy(
        state = BuildInitSettlementState( controller ),
        turn = Turn( state.beginner.get )
      ), Option.empty )
  }

  override def undoStep( game:Game ):Game = game.setState( state )

  override def toString:String = getClass.getSimpleName + ": " + state
}
