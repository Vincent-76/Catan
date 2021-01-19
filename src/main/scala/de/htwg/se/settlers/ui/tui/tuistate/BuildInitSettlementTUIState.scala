package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Settlement
import de.htwg.se.settlers.model.state.BuildInitSettlementState
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI, TUIState }

/**
 * @author Vincent76;
 */
class BuildInitSettlementTUIState( controller: Controller
                                 ) extends BuildInitSettlementState( controller ) with TUIState {

  override def getGameDisplay:Option[String] = {
    Some( GameDisplay( controller, Settlement, controller.onTurn, any = true ).buildGameField )
  }

  override def getActionInfo:String ={
    TUI.outln( TUI.displayName( controller.game.player ) + " place your settlement." )
    "Select position [<id>] for your settlement"
  }

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit = buildInitSettlement( commandInput.command.get.toInt )
}
