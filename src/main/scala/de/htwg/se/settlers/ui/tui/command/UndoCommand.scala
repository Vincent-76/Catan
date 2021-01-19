package de.htwg.se.settlers.ui.tui.command

import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.ui.tui.{ CommandAction, CommandInput }

/**
 * @author Vincent76;
 */
case object UndoCommand
  extends CommandAction( "undo", List.empty, "Undo your last action." ) {

  override def action( commandInput:CommandInput, state:State ):Unit = state.undo()
}
