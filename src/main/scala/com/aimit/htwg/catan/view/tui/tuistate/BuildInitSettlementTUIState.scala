package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info
import com.aimit.htwg.catan.model.impl.placement.SettlementPlacement

import scala.util.Try

/**
 * @author Vincent76;
 */
case class BuildInitSettlementTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game, Some( SettlementPlacement.getBuildablePoints( controller.game, controller.onTurn, any = true ) ) ).buildGameField
  )

  override def createStateDisplay:Iterable[String] =
    List( TUI.displayName( controller.game.player ) + " place your settlement." )

  override def getActionInfo:String = "Select position [<id>] for your settlement"

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) =
    (controller.action( _.buildInitSettlement( commandInput.command.get.toInt ) ), Nil)
}
