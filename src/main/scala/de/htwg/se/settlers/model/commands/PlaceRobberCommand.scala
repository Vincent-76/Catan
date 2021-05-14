package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.state.{ RobberPlaceState, RobberStealState }

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class PlaceRobberCommand( hID:Int, state:RobberPlaceState ) extends RobberCommand {

  private var actualRobber:Option[Hex] = None

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    val hex = game.gameField.findHex( hID )
    if ( hex.isEmpty )
      Failure( NonExistentPlacementPoint( hID ) )
    else if ( hex.get == game.gameField.robber )
      Failure( PlacementPointNotEmpty( hID ) )
    else if ( !hex.get.isLand )
      Failure( RobberOnlyOnLand )
    else {
      actualRobber = Some( game.gameField.robber )
      val newGameField = game.gameField.copy( robber = hex.get )
      newGameField.adjacentPlayers( hex.get ).filter( _ != game.onTurn ) match {
        case Nil => Success( game.copy(
          state = state.nextState,
          gameField = newGameField
        ), None )
        case List( stealPlayerID ) => steal( game, stealPlayerID, state.nextState, Some( newGameField ) )
        case adjacentPlayers => Success( game.copy(
          state = RobberStealState( adjacentPlayers, state.nextState ),
          gameField = newGameField
        ), None )
      }
    }
  }

  override def undoStep( game:Game ):Game = {
    val h = game.gameField.findHex( hID ).get
    val newGameField = if( actualRobber.isDefined ) game.gameField.copy( robber = actualRobber.get ) else game.gameField
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

  /*override def toString:String = getClass.getSimpleName + ": hID[" + hID + "], actualRobber[" + actualRobber.useOrElse( _.id, -1 ) +
    "], robbedResources[" + robbedResource.useOrElse( r => r, "-" ) + "], " + state*/
}
