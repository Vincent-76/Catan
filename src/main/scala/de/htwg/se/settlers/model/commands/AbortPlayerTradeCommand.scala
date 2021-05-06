package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.{ Command, Game, Info }
import de.htwg.se.settlers.model.state.{ ActionState, PlayerTradeEndState }

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class AbortPlayerTradeCommand( state:PlayerTradeEndState ) extends Command {

  override def doStep( game:Game ):Try[(Game, Option[Info])] = Success(
    game.setState( ActionState() ),
    None
  )

  override def undoStep( game:Game ):Game = game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": " + state
}
