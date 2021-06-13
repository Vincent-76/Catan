package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.model.{ Command, Game }
import de.htwg.se.settlers.model.state.{ ActionState, PlayerTradeEndState }

import scala.util.Try

/**
 * @author Vincent76;
 */
case class AbortPlayerTradeCommand( state:PlayerTradeEndState ) extends Command {

  override def doStep( game:Game ):Try[CommandSuccess] = success(
    game.setState( ActionState() )
  )

  override def undoStep( game:Game ):Game = game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": " + state
}
