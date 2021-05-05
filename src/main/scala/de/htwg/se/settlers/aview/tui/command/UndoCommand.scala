package de.htwg.se.settlers.aview.tui.command

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.aview.tui.{ CommandAction, CommandInput }

/**
 * @author Vincent76;
 */
case object UndoCommand
  extends CommandAction( "undo", List.empty, "Undo your last action." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit =
    controller.undoAction()
}
