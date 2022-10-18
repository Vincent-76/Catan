package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info
import com.aimit.htwg.catan.model.impl.placement.RobberPlacement

import scala.util.Try

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

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) =
    (controller.placeRobber( commandInput.input.toInt ), Nil)
}
