package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.state.RobberStealState

import scala.util.{ Failure, Try }

/**
 * @author Vincent76;
 */
case class RobberStealCommand( stealPlayerID:PlayerID, state:RobberStealState ) extends RobberCommand {

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    if ( !game.playerHasAdjacentVertex( stealPlayerID, game.gameField.adjacentVertices( game.gameField.robber ) ) )
      Failure( NoAdjacentStructure )
    else steal( game, stealPlayerID, state.nextState )
  }

  override def undoStep( game:Game ):Game = robbedResource match {
    case Some( r ) => game.copy(
      state = state,
      players = game.players.updated( game.onTurn, game.player.removeResourceCard( r ).get )
        .updated( stealPlayerID, game.players( stealPlayerID ).addResourceCard( r ) )
    )
    case None => game.setState( state )
  }

  //override def toString:String = getClass.getSimpleName + ": robbedResource[" + robbedResource.useOrElse( r => r, "-" ) +  "], stealPlayerID[" + stealPlayerID + "], " + state
}
