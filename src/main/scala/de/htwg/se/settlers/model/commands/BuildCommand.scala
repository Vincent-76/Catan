package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ BuiltInfo, City, Command, Game, Info, Road, Settlement }
import de.htwg.se.settlers.model.state.{ ActionState, BuildState }
import de.htwg.se.settlers.util._

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class BuildCommand( id:Int, state:BuildState ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] =
    state.structure.build( game, game.onTurn, id ) match {
      case Success( newGame ) => Success(
        newGame.setState( ActionState( controller ) ),
        Some( BuiltInfo( state.structure, id ) )
      )
      case f => f.rethrow
    }

  override def undoStep( game:Game ):Game = {
    val newGameField = state.structure match {
      case Road => game.gameField.update( game.gameField.findEdge( id ).get.setRoad( Option.empty ) )
      case Settlement => game.gameField.update( game.gameField.findVertex( id ).get.setBuilding( Option.empty ) )
      case City => game.gameField.update( game.gameField.findVertex( id ).get.setBuilding( Some( Settlement( game.onTurn ) ) ) )
      case _ => game.gameField
    }
    game.copy(
      state = state,
      gameField = newGameField,
      players = game.updatePlayers( game.player.addStructure( state.structure ) )
    )
  }
}
