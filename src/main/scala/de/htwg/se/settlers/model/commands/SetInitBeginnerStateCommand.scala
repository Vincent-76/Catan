package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.state.InitBeginnerState
import de.htwg.se.settlers.model.{Command, Game, Info, NotEnoughPlayers, State}

import scala.util.{Failure, Success, Try}

case class SetInitBeginnerStateCommand(state:State ) extends Command {

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    if( game.players.size >= Game.minPlayers )
      Success( game.setState( InitBeginnerState() ), None )
    else
      Failure( NotEnoughPlayers )
  }

  override def undoStep( game: Game ):Game = game.setState( state )
}
