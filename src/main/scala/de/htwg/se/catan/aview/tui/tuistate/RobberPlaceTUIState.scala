package de.htwg.se.catan.aview.tui.tuistate

import de.htwg.se.catan.aview.tui.{ CommandInput, GameFieldDisplay, TUIState }
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.impl.placement.RobberPlacement

/**
 * @author Vincent76;
 */
case class RobberPlaceTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game, Some( RobberPlacement.getBuildablePoints( controller.game, controller.onTurn ) ) ).buildGameField
      + buildPlayerDisplay( controller.game, Some( controller.game.onTurn ) )
  )

  override def getActionInfo:String = "Select hex [<id>] for the robber"

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit =
    controller.placeRobber( commandInput.input.toInt )
}
