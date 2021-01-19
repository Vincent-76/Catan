package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.state.InitState
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI, TUIState }

/**
 * @author Vincent76;
 */
class InitTUIState( controller:Controller ) extends InitState( controller ) with TUIState {

  override def getActionInfo:String = {
    TUI.outln( "Welcome to Settlers of Catan." )
    TUI.outln( "Type [help] for a list of all available commands." )
    "Press Enter to add players"
  }

  override def action( commandInput:CommandInput ):Unit = initPlayers()

}
