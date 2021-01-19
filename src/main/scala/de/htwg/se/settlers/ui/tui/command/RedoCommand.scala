package de.htwg.se.settlers.ui.tui.command

import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.ui.tui.{ CommandAction, CommandInput }

/**
 * @author Vincent76;
 */
case object RedoCommand
  extends CommandAction( "redo", List.empty, "Redo your last undone action." ) {

  override def action( commandInput:CommandInput, state:State ):Unit = state.redo()
}
