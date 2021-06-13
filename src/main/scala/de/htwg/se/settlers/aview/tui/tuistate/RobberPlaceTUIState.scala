package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.aview.tui.{ CommandInput, TUIState }
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.impl.placement.RobberPlacement

/**
 * @author Vincent76;
 */
case class RobberPlaceTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = {
    val gameDisplay = getGameDisplay( controller, RobberPlacement.getBuildablePoints( controller.game, controller.onTurn ) )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.game.onTurn ) ) )
  }

  override def getActionInfo:String = "Select hex [<id>] for the robber"

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit =
    controller.placeRobber( commandInput.input.toInt )
}
