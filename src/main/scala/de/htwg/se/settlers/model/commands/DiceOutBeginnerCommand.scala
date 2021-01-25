package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.state.{ BuildInitSettlementState, InitBeginnerState }
import de.htwg.se.settlers.model.{ Command, _ }

import scala.util.{ Failure, Random, Success, Try }

/**
 * @author Vincent76;
 */
case class DiceOutBeginnerCommand( state:InitBeginnerState ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    if ( state.beginner.isDefined )
      Failure( UniqueBeginnerExists )
    else {
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
      if ( beginners > 1 )
        Success( game.setState( InitBeginnerState( controller, Option.empty, values, state.counter + 1 ) ), Option.empty )
      else
        Success( game.setState( InitBeginnerState( controller, Some( maxValue._1 ), values ) ), Option.empty )
    }
  }

  override def undoStep( game:Game ):Game = game.setState( state )
}
