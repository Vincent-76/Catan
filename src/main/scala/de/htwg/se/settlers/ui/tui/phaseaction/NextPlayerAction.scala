package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }

/**
 * @author Vincent76;
 */
case class NextPlayerAction( controller:Controller ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + "\n" + gameDisplay.buildPlayerDisplay() )
  }

  override def actionInfo:Option[String] = {
    TUI.outln( TUI.displayName( controller.player ) + "'s turn." )
    Some( "Press Enter to proceed" )
  }

  override def action( commandInput:CommandInput ):Option[Throwable] = controller.setTurnStartPhase()

}
