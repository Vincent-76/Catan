package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.view.tui.{ CommandInput, TUI, TUIState }

/**
 * @author Vincent76;
 */
case class ErrorTUIState( controller:Controller ) extends TUIState {
  override def getActionInfo:String = {
    TUI.outln( "Error!" )
    "Press Enter to undo"
  }

  override def action( commandInput:CommandInput ):Unit = controller.undoAction()
}
