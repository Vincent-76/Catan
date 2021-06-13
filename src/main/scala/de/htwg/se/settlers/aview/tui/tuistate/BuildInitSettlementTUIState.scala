package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.aview.tui.{ CommandInput, TUI, TUIState }
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.impl.placement.SettlementPlacement

/**
 * @author Vincent76;
 */
case class BuildInitSettlementTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = {
    Some( getGameDisplay( controller, SettlementPlacement.getBuildablePoints( controller.game, controller.onTurn, any = true ) ).buildGameField )
  }

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player ) + " place your settlement." )
    "Select position [<id>] for your settlement"
  }

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit =
    controller.buildInitSettlement( commandInput.command.get.toInt )
}
