package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.state.InitBeginnerState
import de.htwg.se.settlers.model._

import scala.util.{ Random, Success, Try }

/**
 * @author Vincent76;
 */
case class DiceOutBeginnerCommand( state:InitBeginnerState ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    val values = if ( state.diceValues.isEmpty ) {
      val r = new Random( game.seed * 1000 )
      game.players.map( d => (d._1, game.rollDice( r )) )
    } else {
      val max = state.diceValues.maxBy( _._2 )._2
      val r = new Random( game.seed * state.counter * -1 )
      state.diceValues.map( d => (d._1, if ( d._2 < max ) 0 else game.rollDice( r )) )
    }
    val maxValue = values.maxBy( _._2 )
    val beginners = values.count( _._2 >= maxValue._2 )
    if ( beginners > 1 ) {
      Success( game.setState( controller.ui.getInitBeginnerState( values, state.counter + 1 ) ), Option.empty )
    } else
      Success( game.copy(
        state = controller.ui.getBuildInitSettlementState,
        turn = Turn( maxValue._1 )
      ), Some( BeginnerInfo( maxValue._1, values ) ) )
  }

  override def undoStep( game:Game ):Game = game.setState( state )
}
