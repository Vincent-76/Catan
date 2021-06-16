package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.aview.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import de.htwg.se.settlers.controller.Controller

/**
 * @author Vincent76;
 */
case class YearOfPlentyTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game, Some( controller.game.onTurn ) )
  )

  override def getActionInfo:String = {
    TUI.outln( "You can specify 2 resources to get from the bank." )
    "Type [" + TUI.resourcePatternInfo + ", ...] to specify resources"
  }

  override def inputPattern:Option[String] = Some( "((^|,)" + TUI.resourcePattern + ")+" )

  override def action( commandInput:CommandInput ):Unit =
    controller.yearOfPlentyAction( TUI.parseResources( commandInput.input ) )
}
