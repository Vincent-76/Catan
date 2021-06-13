package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.Cards._
import de.htwg.se.settlers.model.state.{ PlayerTradeEndState, PlayerTradeState }
import de.htwg.se.settlers.model.{ Command, Game, InsufficientResources, State }

import scala.util.{ Failure, Try }

/**
 * @author Vincent76;
 */
case class SetPlayerTradeStateCommand( give:ResourceCards, get:ResourceCards, state:State ) extends Command {

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( !game.player.hasResources( give ) )
      Failure( InsufficientResources )
    else {
      val decisions = game.players.values.filter( p => p != game.player && !p.hasResources( get ) ).map( p => (p.id, false) ).toMap
      val nextState = game.getNextTradePlayerInOrder( decisions ) match {
        case Some( pID ) => PlayerTradeState( pID, give, get, decisions )
        case None => PlayerTradeEndState( give, get, decisions )
      }
      success( game.setState( nextState ) )
    }
  }

  override def undoStep( game:Game ):Game = game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": give[" + give + "], get[" + get + "], " + state
}
