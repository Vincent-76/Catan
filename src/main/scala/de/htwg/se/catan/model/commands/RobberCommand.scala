package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.model._

import scala.util.Try

/**
 * @author Vincent76;
 */
abstract class RobberCommand( ) extends Command {

  var robbedResource:Option[Resource] = None

  protected def steal( game:Game, stealPlayerID:PlayerID, nextState:State, gameField:Option[GameField] = None ):Try[CommandSuccess] = {
    robbedResource = game.players( stealPlayerID ).randomHandResource()
    robbedResource match {
      case Some( r ) => success(
        game.setState( nextState )
          .setGameField( gameField.getOrElse( game.gameField ) )
          .updatePlayers(
            game.players( stealPlayerID ).removeResourceCard( r ).get,
            game.player.addResourceCard( r )
          ),
        info = Some( ResourceChangeInfo(
          playerAdd = Map( game.onTurn -> ResourceCards.ofResource( r ) ),
          playerSub = Map( stealPlayerID -> ResourceCards.ofResource( r ) )
        ) ) )
      case None => success( game.setState( nextState )
        .setGameField( gameField.getOrElse( game.gameField ) )
      )

    }
  }

  //override def toString:String = getClass.getSimpleName + ": robbedResource[" + robbedResource.useOrElse( r => r, "-" ) + "]"
}
