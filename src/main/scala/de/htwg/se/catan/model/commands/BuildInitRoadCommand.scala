package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.impl.placement.RoadPlacement
import de.htwg.se.catan.model.state.{ BuildInitRoadState, BuildInitSettlementState, NextPlayerState }
import de.htwg.se.catan.model.{ Command, Game, InvalidPlacementPoint }
import de.htwg.se.catan.util._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class BuildInitRoadCommand( eID:Int, state:BuildInitRoadState ) extends Command {

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( !game.gameField.adjacentEdges( game.gameField.findVertex( state.settlementVID ).get ).exists( _.id == eID ) )
      Failure( InvalidPlacementPoint( eID ) )
    else RoadPlacement.build( game, game.onTurn, eID ) match {
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
        success( game.setState( nState )
          .setTurn( game.turn.set( nTurn ) )
        )
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
    game.setState( state )
      .setGameField( game.gameField.update( game.gameField.findEdge( eID ).get.setRoad( None ) ) )
      .setTurn( game.turn.set( nTurn ) )
      .updatePlayer( game.players( nTurn ).addStructure( RoadPlacement ) )
  }

  //override def toString:String = getClass.getSimpleName + ": eID[" + eID + "], " + state
}
