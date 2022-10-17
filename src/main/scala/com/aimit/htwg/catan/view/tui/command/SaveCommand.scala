package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput, TUI }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.util._

/**
 * @author Vincent76;
 */

case object SaveCommand
  extends CommandAction( "save", List.empty, "Saves the game." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit = controller.saveGame()
}
