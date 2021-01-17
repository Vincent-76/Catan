package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ DevRoadBuildingPhase, Road }
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay }

/**
 * @author Vincent76;
 */
case class DevRoadBuildingAction( phase:DevRoadBuildingPhase, controller:Controller ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = Some( GameDisplay( controller, Road, controller.onTurn ).buildGameField )

  override def actionInfo:Option[String] = Some( "Select position [<id>] for your road" )

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Option[Throwable] = controller.devBuildRoad( commandInput.input.toInt )

}
