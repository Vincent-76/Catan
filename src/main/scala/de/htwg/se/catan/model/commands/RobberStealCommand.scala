package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model._
import de.htwg.se.catan.model.state.RobberStealState

import scala.util.{ Failure, Try }

/**
 * @author Vincent76;
 */
case class RobberStealCommand( stealPlayerID:PlayerID, state:RobberStealState ) extends RobberCommand {

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( !game.playerHasAdjacentVertex( stealPlayerID, game.gameField.adjacentVertices( game.gameField.robberHex ) ) )
      Failure( NoAdjacentStructure )
    else steal( game, stealPlayerID, state.nextState )
  }

  override def undoStep( game:Game ):Game = robbedResource match {
    case Some( r ) => game.setState( state )
      .updatePlayers(
        game.player.removeResourceCard( r ).get,
        game.players( stealPlayerID ).addResourceCard( r )
      )
    case None => game.setState( state )
  }

  //override def toString:String = getClass.getSimpleName + ": robbedResource[" + robbedResource.useOrElse( r => r, "-" ) +  "], stealPlayerID[" + stealPlayerID + "], " + state
}
