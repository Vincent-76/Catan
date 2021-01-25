package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Command, Game, Info }
import de.htwg.se.settlers.model.state.{ ActionState, PlayerTradeEndState }

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class AbortPlayerTradeCommand( state:PlayerTradeEndState ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = Success(
    game.setState( ActionState( controller ) ),
    Option.empty
  )

  override def undoStep( game:Game ):Game = game.setState( state )
}
