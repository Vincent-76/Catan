package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.command.UseDevCommand
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI, TUIState }

/**
 * @author Vincent76;
 */
case class DiceTUIState( controller:Controller ) extends TUIState {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.game.onTurn ) ) )
  }

  override def getActionInfo:String = {
    TUI.outln( "Available commands:" )
    TUI.outln( UseDevCommand.desc + "   " + UseDevCommand.getSyntax )
    "Type command, or Enter to roll the dices"
  }

  override def inputPattern:Option[String] =
    Some( "(^$|" + UseDevCommand.inputPattern + ")" )

  override def action( commandInput:CommandInput ):Unit = commandInput.input.toLowerCase match {
    case "" => controller.game.state.rollTheDices()
    case _ => UseDevCommand.action( commandInput, controller )
  }

}
