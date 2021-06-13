package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.state.InitBeginnerState
import de.htwg.se.settlers.model.{ Command, Game, NotEnoughPlayers, State }

import scala.util.{ Failure, Try }

case class SetInitBeginnerStateCommand( state:State ) extends Command {

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( game.players.size >= game.minPlayers )
      success( game.setState( InitBeginnerState() ) )
    else
      Failure( NotEnoughPlayers )
  }

  override def undoStep( game:Game ):Game = game.setState( state )
}
