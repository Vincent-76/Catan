package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model.{ Info, PlayerID }
import com.aimit.htwg.catan.util._

import scala.util.Try

/**
 * @author Vincent76;
 */
case class PlayerTradeTUIState( pID:PlayerID, give:ResourceCards, get:ResourceCards, controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game, Some( pID ) )
  )

  override def createStateDisplay:Iterable[String] = List(
    TUI.displayName( controller.game.player( pID ) ) + ", do you want to trade?",
    "Get:\t" + give.toString,
    "Give:\t" + get.toString
  )

  override def getActionInfo:String = "Type [Y] for yes or [N] for no"

  override def inputPattern:Option[String] = Some( "(" + TUI.regexIgnoreCase( "y" ) + "|" + TUI.regexIgnoreCase( "n" ) + ")" )

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) =
    (controller.action( _.playerTradeDecision( commandInput.input ^= "y" ) ), Nil)
}
