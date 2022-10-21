package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.command.UseDevCommand
import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info

import scala.util.Try

/**
 * @author Vincent76;
 */
case class DiceTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game, Some( controller.game.onTurn ) )
  )

  override def createStateDisplay:Iterable[String] =
    List( "Available commands:", UseDevCommand.desc + "   " + UseDevCommand.getSyntax )

  override def getActionInfo:String = "Type command, or Enter to roll the dices"

  override def inputPattern:Option[String] =
    Some( "(^$|" + UseDevCommand.inputPattern + ")" )

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) = commandInput.input.toLowerCase match {
    case "" => (controller.rollTheDices(), Nil)
    case _ => UseDevCommand.action( commandInput, controller )
  }

}
