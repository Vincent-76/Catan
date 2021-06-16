package de.htwg.se.catan.aview.tui.tuistate

import de.htwg.se.catan.aview.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import de.htwg.se.catan.controller.Controller

/**
 * @author Vincent76;
 */
case class NextPlayerTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game )
  )

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player ) + "'s turn." )
    "Press Enter to proceed"
  }

  override def action( commandInput:CommandInput ):Unit =
    controller.startTurn()
}
