package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ InitBuildRoadPhase, Road }
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }

/**
 * @author Vincent76;
 */
case class InitBuildRoadAction( phase:InitBuildRoadPhase, controller:Controller ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = {
    Some( GameDisplay( controller, Road, controller.game.getBuildableRoadSpotsForSettlement( phase.settlementVID ) ).buildGameField )
  }

  override def actionInfo:Option[String] = {
    TUI.outln( TUI.displayName( controller.player ) + " place your road." )
    Some( "Select position [<id>] for your road" )
  }

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Option[Throwable] = {
    controller.setInitBuildRoad( commandInput.command.get.toInt )
  }
}
