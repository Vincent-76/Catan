package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model.PlayerID
import com.aimit.htwg.catan.util._

/**
 * @author Vincent76;
 */
case class PlayerTradeTUIState( pID:PlayerID, give:ResourceCards, get:ResourceCards, controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game, Some( pID ) )
  )

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player( pID ) ) + ", do you want to trade?" )
    TUI.outln( "Get:\t" + give.toString )
    TUI.outln( "Give:\t" + get.toString )
    "Type [Y] for yes or [N] for no"
  }

  override def inputPattern:Option[String] = Some( "(" + TUI.regexIgnoreCase( "y" ) + "|" + TUI.regexIgnoreCase( "n" ) + ")" )

  override def action( commandInput:CommandInput ):Unit =
    controller.playerTradeDecision( commandInput.input ^= "y" )
}
