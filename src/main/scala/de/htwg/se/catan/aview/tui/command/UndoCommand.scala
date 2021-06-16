package de.htwg.se.catan.aview.tui.command

import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.aview.tui.{ CommandAction, CommandInput }

/**
 * @author Vincent76;
 */
case object UndoCommand
  extends CommandAction( "undo", List.empty, "Undo your last action." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit =
    controller.undoAction()
}
