package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Settlement
import de.htwg.se.settlers.aview.tui.{ CommandInput, GameDisplay, TUI, TUIState }

/**
 * @author Vincent76;
 */
case class BuildInitSettlementTUIState( controller:Controller ) extends TUIState {

  override def getGameDisplay:Option[String] = {
    Some( GameDisplay( controller, Settlement.getBuildablePoints( controller.game, controller.onTurn, any = true ) ).buildGameField )
  }

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player ) + " place your settlement." )
    "Select position [<id>] for your settlement"
  }

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit =
    controller.buildInitSettlement( commandInput.command.get.toInt )
}
