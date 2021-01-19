package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.state.YearOfPlentyState
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI, TUIState }

/**
 * @author Vincent76;
 */
class YearOfPlentyTUIState( nextState:State,
                            controller:Controller
                          ) extends YearOfPlentyState( nextState, controller) with TUIState {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.game.onTurn ) ) )
  }

  override def getActionInfo:String = {
    TUI.outln( "You can specify 2 resources to get from the bank." )
    "Type [" + TUI.resourcePatternInfo + ", ...] to specify resources"
  }

  override def inputPattern:Option[String] = Some( "((^|,)" + TUI.resourcePattern + ")+" )

  override def action( commandInput:CommandInput ):Unit = yearOfPlentyAction( TUI.parseResources( commandInput.input ) )
}
