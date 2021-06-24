package de.htwg.se.catan.aview.tui.command

import de.htwg.se.catan.aview.tui.{ CommandAction, CommandInput, TUI }
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.util._

/**
 * @author Vincent76;
 */

case object SaveCommand
  extends CommandAction( "save", List.empty, "Saves the game." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit = controller.saveGame()
}
