package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI, TUIState }

/**
 * @author Vincent76;
 */
case class ErrorTUIState( controller: Controller ) extends TUIState{
  override def getActionInfo:String = {
    TUI.outln( "Error!" )
    "Press Enter to undo"
  }

  override def action( commandInput:CommandInput ):Unit = controller.undoAction()
}
