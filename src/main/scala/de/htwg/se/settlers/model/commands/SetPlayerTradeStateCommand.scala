package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.state.{ PlayerTradeEndState, PlayerTradeState }
import de.htwg.se.settlers.model.{ Command, Game, Info, InsufficientResources, State }
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class SetPlayerTradeStateCommand( give:ResourceCards, get:ResourceCards, state:State ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    if ( !game.player.resources.has( give ) )
      Failure( InsufficientResources )
    else {
      val decisions = game.players.values.filter( p => p != game.player && !p.resources.has( get ) ).map( p => (p.id, false) ).toMap
      val nextState = game.getNextTradePlayerInOrder( decisions ) match {
        case Some( pID ) => PlayerTradeState( controller, pID, give, get, decisions )
        case None => PlayerTradeEndState( controller, give, get, decisions )
      }
      Success( game.setState( nextState ), Option.empty )
    }
  }

  override def undoStep( game:Game ):Game = game.setState( state )
}
