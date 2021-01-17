package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI }

/**
 * @author Vincent76;
 */
case class InitAction( controller:Controller ) extends PhaseAction( controller ) {

  override def actionInfo:Option[String] = {
    TUI.outln( "Welcome to Settlers of Catan." )
    TUI.outln( "Type [help] for a list of all available commands." )
    Some( "Press Enter to add players" )
  }

  override def action( commandInput:CommandInput ):Option[Throwable] = controller.initPlayerPhase()
}
