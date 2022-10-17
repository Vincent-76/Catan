package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput }

/**
 * @author Vincent76;
 */
case object UndoCommand
  extends CommandAction( "undo", List.empty, "Undo your last action." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit =
    controller.undoAction()
}
