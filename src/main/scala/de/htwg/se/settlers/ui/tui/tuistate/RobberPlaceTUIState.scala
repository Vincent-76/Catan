package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Robber
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUIState }

/**
 * @author Vincent76;
 */
case class RobberPlaceTUIState( controller:Controller ) extends TUIState {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller, Robber.getBuildablePoints( controller.game, controller.onTurn ) )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.game.onTurn ) ) )
  }

  override def getActionInfo:String = "Select hex [<id>] for the robber"

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit =
    controller.game.state.placeRobber( commandInput.input.toInt )
}
