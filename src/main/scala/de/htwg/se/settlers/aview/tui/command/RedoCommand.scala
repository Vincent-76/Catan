package de.htwg.se.settlers.aview.tui.command

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.aview.tui.{ CommandAction, CommandInput }

/**
 * @author Vincent76;
 */
case object RedoCommand
  extends CommandAction( "redo", List.empty, "Redo your last undone action." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit =
    controller.redoAction()
}
