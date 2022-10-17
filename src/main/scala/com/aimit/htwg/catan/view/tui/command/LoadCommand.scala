package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput, TUI }
import com.aimit.htwg.catan.controller.Controller

/**
 * @author Vincent76;
 */

case object LoadCommand
  extends CommandAction( "load", List( "path_to_savegame" ), "Loads a savegame." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit =
    controller.loadGame( commandInput.input.substring( command.length ).trim )

  override protected def getInputPattern:String = {
    TUI.regexIgnoreCase( command ) + "\\s+.*"
  }
}
