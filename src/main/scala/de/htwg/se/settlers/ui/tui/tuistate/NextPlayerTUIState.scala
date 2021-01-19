package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.state.NextPlayerState
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI, TUIState }

/**
 * @author Vincent76;
 */
class NextPlayerTUIState( controller:Controller ) extends NextPlayerState( controller ) with TUIState {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + "\n" + gameDisplay.buildPlayerDisplay() )
  }

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player ) + "'s turn." )
    "Press Enter to proceed"
  }

  override def action( commandInput:CommandInput ):Unit = startTurn()
}
