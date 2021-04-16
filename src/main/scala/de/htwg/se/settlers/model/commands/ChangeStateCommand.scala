package de.htwg.se.settlers.model.commands
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Command, Game, Info, State }
import de.htwg.se.settlers.util._

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class ChangeStateCommand( state:State, nextState:State, info:Option[Info] = Option.empty ) extends Command {

  override def doStep( controller: Controller, game: Game):Try[(Game, Option[Info])] = Success( game.setState( nextState ), info )

  override def undoStep( game:Game ):Game = game.setState( state )

  override def toString:String = getClass.getSimpleName + ": State[" + state + "], NextState[" + nextState + "], Info[" + info.useOrElse( i => i.getClass.getSimpleName, "-" ) + "]"
}
