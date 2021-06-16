package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.state.{ PlayerTradeEndState, PlayerTradeState }
import de.htwg.se.catan.model.{ Command, Game }

import scala.util.Try

/**
 * @author Vincent76;
 */
case class PlayerTradeDecisionCommand( decision:Boolean, state:PlayerTradeState ) extends Command {

  override def doStep( game:Game ):Try[CommandSuccess] = {
    val decisions = state.decisions.updated( state.pID, decision )
    success( game.getNextTradePlayerInOrder( decisions, state.pID ) match {
      case Some( pID ) => game.setState( PlayerTradeState( pID, state.give, state.get, decisions ) )
      case None => game.setState( PlayerTradeEndState( state.give, state.get, decisions ) )
    }, None )
  }

  override def undoStep( game:Game ):Game = game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": Decision[" + decision + "], " + state
}
