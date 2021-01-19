package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.state.{ BuildInitRoadState, NextPlayerState }
import de.htwg.se.settlers.model.{ Command, Game, Info, InvalidPlacementPoint, Road, Turn }
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class BuildInitRoadCommand( eID:Int, state:BuildInitRoadState ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    if ( !game.gameField.adjacentEdges( game.gameField.findVertex( state.settlementVID ).get ).exists( _.id == eID ) )
      Failure( InvalidPlacementPoint )
    else Road.build( game, game.onTurn, eID ) match {
      case Success( game ) =>
        val (nTurn, nState) = game.settlementAmount( game.onTurn ) match {
          case 1 => game.settlementAmount( game.nextTurn() ) match {
            case 0 => (game.nextTurn(), controller.ui.getBuildInitSettlementState)
            case _ => (game.onTurn, controller.ui.getBuildInitSettlementState)
          }
          case _ => game.settlementAmount( game.previousTurn() ) match {
            case 1 => (game.previousTurn(), controller.ui.getBuildInitSettlementState)
            case _ => (game.onTurn, controller.ui.getNextPlayerState)
          }
        }
        Success( game.copy(
          state = nState,
          turn = Turn( nTurn )
        ), Option.empty )
      case f => f.rethrow
    }
  }

  override def undoStep( game:Game ):Game = {
    val nTurn = game.state match {
      case _:NextPlayerState => game.onTurn
      case _ => game.settlementAmount( game.onTurn ) match {
        case 0 => game.previousTurn()
        case 1 => game.settlementAmount( game.nextTurn() ) match {
          case 1 => game.onTurn
          case 2 => game.nextTurn()
        }
      }
    }
    game.copy(
      state = state,
      gameField = game.gameField.update( game.gameField.findEdge( eID ).get.setRoad( Option.empty ) ),
      turn = Turn( nTurn ),
      players = game.updatePlayers( game.players( nTurn ).addStructure( Road ) )
    )
  }
}
