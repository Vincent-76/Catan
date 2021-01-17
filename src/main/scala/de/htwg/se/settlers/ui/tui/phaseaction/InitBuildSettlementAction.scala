package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Settlement
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }

/**
 * @author Vincent76;
 */
case class InitBuildSettlementAction( controller:Controller ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = {
    Some( GameDisplay( controller, Settlement, controller.onTurn, any = true ).buildGameField )
  }

  override def actionInfo:Option[String] = {
    TUI.outln( TUI.displayName( controller.player ) + " place your settlement." )
    Some( "Select position [<id>] for your settlement" )
  }

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Option[Throwable] = {
    controller.setInitBuildSettlement( controller.onTurn, commandInput.command.get.toInt )
  }
}
