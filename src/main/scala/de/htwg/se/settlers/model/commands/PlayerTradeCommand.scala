package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.state.{ ActionState, PlayerTradeEndState }
import de.htwg.se.settlers.model.{ Command, _ }

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class PlayerTradeCommand( tradePlayerID:PlayerID, state:PlayerTradeEndState ) extends Command {

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    if ( !state.decisions.getOrElse( tradePlayerID, false ) )
      Failure( InvalidPlayer( tradePlayerID ) )
    else game.player.trade( state.get, state.give ) match {
      case Failure( _ ) => Failure( InsufficientResources )
      case Success( newPlayer ) => game.player( tradePlayerID ).trade( state.give, state.get ) match {
        case Failure( _ ) => Failure( TradePlayerInsufficientResources )
        case Success( tradePlayer ) => Success( game.copy(
          state = ActionState(),
          players = game.updatePlayers( newPlayer, tradePlayer )
        ), Some( ResourceChangeInfo(
          playerAdd = Map( newPlayer.id -> state.get, tradePlayer.id -> state.give ),
          playerSub = Map( newPlayer.id -> state.give, tradePlayer.id -> state.get )
        ) ) )
      }
    }
  }

  override def undoStep( game:Game ):Game = {
    val newPlayer = game.player.trade( state.give, state.get ).get
    val tradePlayer = game.player( tradePlayerID ).trade( state.get, state.give ).get
    game.copy(
      state = state,
      players = game.updatePlayers( newPlayer, tradePlayer )
    )
  }

  //override def toString:String = getClass.getSimpleName + ": tradePlayerID[" + tradePlayerID + "], " + state
}
