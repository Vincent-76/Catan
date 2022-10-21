package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info

import scala.util.Try

/**
 * @author Vincent76;
 */
case class YearOfPlentyTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game, Some( controller.game.onTurn ) )
  )

  override def createStateDisplay:Iterable[String] = List(
    "You can specify 2 resources to get from the bank."
  )

  override def getActionInfo:String = "Type [" + TUI.resourcePatternInfo + ", ...] to specify resources"

  override def inputPattern:Option[String] = Some( "((^|,)" + TUI.resourcePattern + ")+" )

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) =
    (controller.yearOfPlentyAction( TUI.parseResources( commandInput.input ) ), Nil)
}
