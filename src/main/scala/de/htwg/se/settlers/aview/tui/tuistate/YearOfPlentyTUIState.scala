package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.aview.tui.{ CommandInput, GameDisplay, TUI, TUIState }

/**
 * @author Vincent76;
 */
case class YearOfPlentyTUIState( controller:Controller ) extends TUIState {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.game.onTurn ) ) )
  }

  override def getActionInfo:String = {
    TUI.outln( "You can specify 2 resources to get from the bank." )
    "Type [" + TUI.resourcePatternInfo + ", ...] to specify resources"
  }

  override def inputPattern:Option[String] = Some( "((^|,)" + TUI.resourcePattern + ")+" )

  override def action( commandInput:CommandInput ):Unit =
    controller.game.state.yearOfPlentyAction( TUI.parseResources( commandInput.input ) )
}