package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.GameField.Hex
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.state.{ RobberPlaceState, RobberStealState }

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class PlaceRobberCommand( hID:Int, actualRobber:Hex, state:RobberPlaceState ) extends RobberCommand {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    val hex = game.gameField.findHex( hID )
    if ( hex.isEmpty )
      Failure( NonExistentPlacementPoint( hID ) )
    else if ( actualRobber != game.gameField.robber )
      Failure( InconsistentData )
    else if ( hex.get == actualRobber )
      Failure( PlacementPointNotEmpty( hID ) )
    else if ( !hex.get.area.isInstanceOf[LandArea] )
      Failure( RobberOnlyOnWater )
    else {
      val newGameField = game.gameField.copy( robber = hex.get )
      game.gameField.adjacentPlayers( hex.get ).filter( _ != game.onTurn ) match {
        case Nil => Success( game.copy(
          state = state.nextState,
          gameField = newGameField
        ), Option.empty )
        case List( stealPlayerID ) => steal( game, stealPlayerID, state.nextState, Some( newGameField ) )
        case adjacentPlayers => Success( game.copy(
          state = RobberStealState( adjacentPlayers, controller, state.nextState ),
          gameField = newGameField
        ), Option.empty )
      }
    }
  }

  override def undoStep( game:Game ):Game = {
    val h = game.gameField.findHex( hID ).get
    val newGameField = game.gameField.copy( robber = actualRobber )
    game.gameField.adjacentPlayers( h ).filter( _ != game.onTurn ) match {
      case List( stealPlayerID ) if robbedResource.isDefined => game.copy(
        state = state,
        gameField = newGameField,
        players = game.players.updated( game.onTurn, game.player.removeResourceCard( robbedResource.get ).get )
          .updated( stealPlayerID, game.players( stealPlayerID ).addResourceCard( robbedResource.get ) )
      )
      case _ => game.copy(
        state = state,
        gameField = newGameField
      )
    }
  }

}
