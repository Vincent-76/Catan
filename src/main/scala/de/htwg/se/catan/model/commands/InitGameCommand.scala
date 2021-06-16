package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.state.{ InitPlayerState, InitState }
import de.htwg.se.catan.model.{ Command, Game, Info }

import scala.util.Try

case class InitGameCommand() extends Command {

  def doStep( game:Game ):Try[(Game, Option[Info])] = success(
    game.setState( InitPlayerState() )
  )

  def undoStep( game:Game ):Game = game.setState( InitState() )
}
