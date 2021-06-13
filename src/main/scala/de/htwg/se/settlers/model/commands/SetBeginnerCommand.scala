package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.state.{ BuildInitSettlementState, InitBeginnerState }
import de.htwg.se.settlers.model.{ Command, Game, NoUniqueBeginner, Turn }

import scala.util.{ Failure, Try }

/**
 * @author Vincent76;
 */
case class SetBeginnerCommand( state:InitBeginnerState ) extends Command {

  var oldTurn:Option[Turn] = None

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( state.beginner.isEmpty )
      Failure( NoUniqueBeginner )
    else {
      oldTurn = Some( game.turn )
      success( game.setState( BuildInitSettlementState() )
        .setTurn( game.turn.set( state.beginner.get ) )
      )
    }
  }

  override def undoStep( game:Game ):Game = game.setState( state )
    .setTurn( oldTurn.getOrElse( game.turn ) )

  //override def toString:String = getClass.getSimpleName + ": " + state
}
