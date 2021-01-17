package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.BuildPhase
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay }

/**
 * @author Vincent76;
 */
case class BuildAction( buildPhase:BuildPhase, controller:Controller ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = {
    Some( GameDisplay( controller, buildPhase.structure, controller.onTurn ).buildGameField )
  }

  override def actionInfo:Option[String] = Some( "Select position [<id>] for your " + buildPhase.structure.s )

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Option[Throwable] =
    controller.build( buildPhase.structure, commandInput.input.toInt )

}
