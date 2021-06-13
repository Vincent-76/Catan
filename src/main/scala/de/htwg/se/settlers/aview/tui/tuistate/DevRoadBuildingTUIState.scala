package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.aview.tui.{ CommandInput, TUIState }
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.impl.placement.RoadPlacement

/**
 * @author Vincent76;
 */
case class DevRoadBuildingTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    getGameDisplay( controller, RoadPlacement.getBuildablePoints( controller.game, controller.onTurn ) ).buildGameField )

  override def getActionInfo:String = "Select position [<id>] for your road"

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit =
    controller.devBuildRoad( commandInput.input.toInt )
}
