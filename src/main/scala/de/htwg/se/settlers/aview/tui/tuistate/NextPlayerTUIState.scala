package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.aview.tui.{ CommandInput, TUI, TUIState }
import de.htwg.se.settlers.controller.Controller

/**
 * @author Vincent76;
 */
case class NextPlayerTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = {
    val gameDisplay = getGameDisplay( controller )
    Some( gameDisplay.buildGameField + "\n" + gameDisplay.buildPlayerDisplay() )
  }

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player ) + "'s turn." )
    "Press Enter to proceed"
  }

  override def action( commandInput:CommandInput ):Unit =
    controller.startTurn()
}
