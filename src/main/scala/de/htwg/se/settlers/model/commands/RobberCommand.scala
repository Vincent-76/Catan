package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.cards.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.{ Command, Game, ClassicGameField, Info, Resource, ResourceChangeInfo, State }

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
abstract class RobberCommand( ) extends Command {

  var robbedResource:Option[Resource] = None

  protected def steal( game:Game, stealPlayerID:PlayerID, nextState:State, gameField:Option[ClassicGameField] = None ):Try[(Game, Option[Info])] = {
    robbedResource = game.players( stealPlayerID ).randomHandResource()
    robbedResource match {
      case Some( r ) => Success( game.copy(
        state = nextState,
        gameField = gameField.getOrElse( game.gameField ),
        players = game.players.updated( stealPlayerID, game.players( stealPlayerID ).removeResourceCard( r ).get )
          .updated( game.onTurn, game.player.addResourceCard( r ) )
      ), Some( ResourceChangeInfo(
        playerAdd = Map( game.onTurn -> ResourceCards.ofResource( r ) ),
        playerSub = Map( stealPlayerID -> ResourceCards.ofResource( r ) )
      ) ) )
      case None => Success( game.copy(
        state = nextState,
        gameField = gameField.getOrElse( game.gameField )
      ), None )
    }
  }

  //override def toString:String = getClass.getSimpleName + ": robbedResource[" + robbedResource.useOrElse( r => r, "-" ) + "]"
}
