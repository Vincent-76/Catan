package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller

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
