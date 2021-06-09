package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.cards.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.aview.tui.{ CommandInput, GameDisplay, TUI, TUIState }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
case class PlayerTradeTUIState( pID:PlayerID, give:ResourceCards, get:ResourceCards, controller:Controller ) extends TUIState {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( pID ) ) )
  }

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player( pID ) ) + ", do you want to trade?" )
    TUI.outln( "Get:\t" + give.toString )
    TUI.outln( "Give:\t" + get.toString )
    "Type [Y] for yes or [N] for no"
  }

  override def inputPattern:Option[String] = Some( "(" + TUI.regexIgnoreCase( "y" ) + "|" + TUI.regexIgnoreCase( "n" ) + ")" )

  override def action( commandInput:CommandInput ):Unit =
    controller.playerTradeDecision( commandInput.input =^ "y" )
}
