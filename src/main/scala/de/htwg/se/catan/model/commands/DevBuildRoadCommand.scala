package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.impl.placement.RoadPlacement
import de.htwg.se.catan.model.state.DevRoadBuildingState
import de.htwg.se.catan.model.{ Command, _ }

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class DevBuildRoadCommand( eID:Int, state:DevRoadBuildingState ) extends Command {

  override def doStep( game:Game ):Try[CommandSuccess] = {
    RoadPlacement.build( game, game.onTurn, eID ) match {
      case Failure( t ) => Failure( t )
      case Success( newGame ) =>
        val (nextState, info) = if ( !newGame.player.hasStructure( RoadPlacement ) )
          (state.nextState, InsufficientStructuresInfo( newGame.onTurn, RoadPlacement ))
        else if ( RoadPlacement.getBuildablePoints( newGame, newGame.onTurn ).isEmpty )
          (state.nextState, NoPlacementPointsInfo( newGame.onTurn, RoadPlacement ))
        else if ( state.roads == 0 )
          (DevRoadBuildingState( state.nextState, state.roads + 1 ), BuiltInfo( RoadPlacement, eID ))
        else
          (state.nextState, BuiltInfo( RoadPlacement, eID ))
        success( newGame.setState( nextState ), info = Some( info ) )
    }
  }

  override def undoStep( game:Game ):Game = game.setState( state )
    .setGameField( game.gameField.update( game.gameField.findEdge( eID ).get.setRoad( None ) ) )
    .updatePlayer( game.player.addStructure( RoadPlacement ) )

  //override def toString:String = getClass.getSimpleName + ": eID[" + eID + "], " + state
}
