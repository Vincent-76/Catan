package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.state.NextPlayerState
import de.htwg.se.settlers.model.{ Command, Game, State, Turn }

import scala.util.Try

/**
 * @author Vincent76;
 */
case class EndTurnCommand( state:State ) extends Command {

  var turn:Option[Turn] = None

  override def doStep( game:Game ):Try[CommandSuccess] = {
    turn = Some( game.turn )
    success( game.setState( NextPlayerState() ).nextRound() )
  }


  override def undoStep( game:Game ):Game = game.setState( state )
    .previousRound( turn )

  //override def toString:String = getClass.getSimpleName + ": " + state + ", turn[" + turn.useOrElse( t => t.playerID, "-" ) + "]"
}
