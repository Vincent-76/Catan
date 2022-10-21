package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info

import scala.util.Try

/**
 * @author Vincent76;
 */
case class NextPlayerTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game )
  )

  override def createStateDisplay:Iterable[String] = List(
    TUI.displayName( controller.game.player ) + "'s turn."
  )

  override def getActionInfo:String = "Press Enter to proceed"

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) =
    (controller.startTurn(), Nil)
}
