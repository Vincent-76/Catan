package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.{ Command, Game, Info }
import de.htwg.se.settlers.model.state.{ PlayerTradeEndState, PlayerTradeState }

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class PlayerTradeDecisionCommand( decision:Boolean, state:PlayerTradeState ) extends Command {

  override def doStep( game:Game ):Try[(Game, Option[Info])] = {
    val decisions = state.decisions.updated( state.pID, decision )
    Success( game.getNextTradePlayerInOrder( decisions, state.pID ) match {
      case Some( pID ) => game.setState( PlayerTradeState( pID, state.give, state.get, decisions ) )
      case None => game.setState( PlayerTradeEndState( state.give, state.get, decisions ) )
    }, Option.empty )
  }

  override def undoStep( game:Game ):Game = game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": Decision[" + decision + "], " + state
}
