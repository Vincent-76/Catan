package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Road
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUIState }

/**
 * @author Vincent76;
 */
case class DevRoadBuildingTUIState( controller:Controller ) extends TUIState {

  override def getGameDisplay:Option[String] = Some(
    GameDisplay( controller, Road.getBuildablePoints( controller.game, controller.onTurn ) ).buildGameField )

  override def getActionInfo:String = "Select position [<id>] for your road"

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit =
    controller.game.state.devBuildRoad( commandInput.input.toInt )
}
