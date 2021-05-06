package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.aview.tui.{ CommandInput, GameDisplay, TUI, TUIState }

/**
 * @author Vincent76;
 */
case class NextPlayerTUIState( controller:Controller ) extends TUIState {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + "\n" + gameDisplay.buildPlayerDisplay() )
  }

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player ) + "'s turn." )
    "Press Enter to proceed"
  }

  override def action( commandInput:CommandInput ):Unit =
    controller.startTurn()
}
