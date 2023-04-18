package de.htwg.se.catan.aview.tui.tuistate

import de.htwg.se.catan.aview.tui.command.UseDevCommand
import de.htwg.se.catan.aview.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import de.htwg.se.catan.controller.Controller

/**
 * @author Vincent76;
 */
case class DiceTUIState( controller:Controller ) extends TUIState:

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game, Some( controller.game.onTurn ) )
  )

  override def getActionInfo:String =
    TUI.outln( "Available commands:" )
    TUI.outln( UseDevCommand.desc + "   " + UseDevCommand.getSyntax )
    "Type command, or Enter to roll the dices"

  override def inputPattern:Option[String] =
    Some( "(^$|" + UseDevCommand.inputPattern + ")" )

  override def action( commandInput:CommandInput ):Unit = commandInput.input.toLowerCase match
    case "" => controller.rollTheDices()
    case _ => UseDevCommand.action( commandInput, controller )