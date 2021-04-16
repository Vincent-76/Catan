package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.state.DevRoadBuildingState
import de.htwg.se.settlers.model.{ Command, _ }

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class DevBuildRoadCommand( eID:Int, state:DevRoadBuildingState ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    Road.build( game, game.onTurn, eID ) match {
      case Failure( t ) => Failure( t )
      case Success( newGame ) =>
        val (nextState, info) = if ( !game.player.hasStructure( Road ) )
          (state.nextState, InsufficientStructuresInfo( game.onTurn, Road ))
        else if ( Road.getBuildablePoints( game, game.onTurn ).isEmpty )
          (state.nextState, NoPlacementPointsInfo( game.onTurn, Road ))
        else if ( state.roads == 0 )
          (DevRoadBuildingState( controller, state.nextState, state.roads + 1 ), BuiltInfo( Road, eID ))
        else
          (state.nextState, BuiltInfo( Road, eID ))
        Success( newGame.setState( nextState ), Some( info ) )
    }
  }

  override def undoStep( game:Game ):Game = game.copy(
      state = state,
      gameField = game.gameField.update( game.gameField.findEdge( eID ).get.setRoad( Option.empty ) ),
      players = game.updatePlayers( game.player.addStructure( Road ) )
    )

  override def toString:String = getClass.getSimpleName + ": eID[" + eID + "], " + state
}
