package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput, TUI }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info

import scala.util.Try

/**
 * @author Vincent76;
 */

case object LoadCommand
  extends CommandAction( "load", List( "path_to_savegame" ), "Loads a savegame." ) {

  override def action( commandInput:CommandInput, controller:Controller ):(Try[Option[Info]], List[String]) =
    (controller.loadGame( commandInput.input.substring( command.length ).trim ), Nil)

  override protected def getInputPattern:String = {
    TUI.regexIgnoreCase( command ) + "\\s+.*"
  }
}
