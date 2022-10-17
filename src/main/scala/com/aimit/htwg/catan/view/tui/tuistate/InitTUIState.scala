package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.view.tui.{ CommandInput, TUI, TUIState }

/**
 * @author Vincent76;
 */
case class InitTUIState( controller:Controller ) extends TUIState {

  override def getActionInfo:String = {
    TUI.outln( "Welcome to Settlers of Catan." )
    TUI.outln( "Type [help] for a list of all available commands." )
    "Press Enter to add players"
  }

  override def action( commandInput:CommandInput ):Unit =
    controller.initGame()

}
