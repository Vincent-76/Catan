package de.htwg.se.catan.aview.tui.command

import de.htwg.se.catan.aview.tui.{ CommandAction, CommandInput, TUI }
import de.htwg.se.catan.controller.Controller

/**
 * @author Vincent76;
 */

case object LoadCommand
  extends CommandAction( "load", List( "path_to_savegame" ), "Loads a savegame." ):

  override def action( commandInput:CommandInput, controller:Controller ):Unit =
    controller.loadGame( commandInput.input.substring( command.length ).trim )

  override protected def getInputPattern:String =
    TUI.regexIgnoreCase( command ) + "\\s+.*"
