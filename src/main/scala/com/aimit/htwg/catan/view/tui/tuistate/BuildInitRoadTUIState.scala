package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info

import scala.util.Try

/**
 * @author Vincent76;
 */
case class BuildInitRoadTUIState( vID:Int, controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game, Some( controller.game.getBuildableRoadSpotsForSettlement( vID ) ) ).buildGameField
  )

  override def createStateDisplay:Iterable[String] =
    List( TUI.displayName( controller.game.player ) + " place your road." )

  override def getActionInfo:String = "Select position [<id>] for your road"

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) =
    (controller.buildInitRoad( commandInput.command.get.toInt ), Nil)
}
