package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.state.{ BuildInitRoadState, BuildInitSettlementState, NextPlayerState }
import de.htwg.se.settlers.model.{ Command, Game, Info, InvalidPlacementPoint, Road, Turn }
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class BuildInitRoadCommand( eID:Int, state:BuildInitRoadState ) extends Command {

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    if ( !game.gameField.adjacentEdges( game.gameField.findVertex( state.settlementVID ).get ).exists( _.id == eID ) )
      Failure( InvalidPlacementPoint( eID ) )
    else Road.build( game, game.onTurn, eID ) match {
      case Success( game ) =>
        val (nTurn, nState) = game.settlementAmount( game.onTurn ) match {
          case 1 => game.settlementAmount( game.nextTurn() ) match {
            case 0 => (game.nextTurn(), BuildInitSettlementState())
            case _ => (game.onTurn, BuildInitSettlementState())
          }
          case _ => game.settlementAmount( game.previousTurn() ) match {
            case 1 => (game.previousTurn(), BuildInitSettlementState())
            case _ => (game.onTurn, NextPlayerState())
          }
        }
        Success( game.copy(
          state = nState,
          turn = Turn( nTurn )
        ), None )
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
      gameField = game.gameField.update( game.gameField.findEdge( eID ).get.setRoad( None ) ),
      turn = Turn( nTurn ),
      players = game.updatePlayers( game.players( nTurn ).addStructure( Road ) )
    )
  }

  //override def toString:String = getClass.getSimpleName + ": eID[" + eID + "], " + state
}
