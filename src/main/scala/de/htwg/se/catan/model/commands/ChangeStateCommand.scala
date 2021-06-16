package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.{ Command, Game, Info, State }

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case class ChangeStateCommand( state:State, nextState:State, info:Option[Info] = None ) extends Command {

  override def doStep( game:Game ):Try[CommandSuccess] = success( game.setState( nextState ), info = info )

  override def undoStep( game:Game ):Game = game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": State[" + state + "], NextState[" + nextState + "], Info[" + info.useOrElse( i => i.getClass.getSimpleName, "-" ) + "]"
}
