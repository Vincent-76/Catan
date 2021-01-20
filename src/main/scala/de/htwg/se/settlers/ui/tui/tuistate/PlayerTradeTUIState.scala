package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.state.PlayerTradeState
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI, TUIState }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
class PlayerTradeTUIState( pID:PlayerID,
                           give:ResourceCards,
                           get:ResourceCards,
                           decisions:Map[PlayerID, Boolean],
                           controller:Controller
                         ) extends PlayerTradeState( pID, give, get, decisions, controller ) with TUIState {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( pID ) ) )
  }

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player( pID ) ) + ", do you want to trade?" )
    TUI.outln( "Get:\t" + TUI.resourceString( give ) )
    TUI.outln( "Give:\t" + TUI.resourceString( get ) )
    "Type [Y] for yes or [N] for no"
  }

  override def inputPattern:Option[String] = Some( "(" + TUI.regexIgnoreCase( "y" ) + "|" + TUI.regexIgnoreCase( "n" ) + ")" )

  override def action( commandInput:CommandInput ):Unit = playerTradeDecision( commandInput.input =^ "y" )
}
