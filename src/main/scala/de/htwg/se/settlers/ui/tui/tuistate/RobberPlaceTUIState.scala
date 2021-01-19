package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Robber, State }
import de.htwg.se.settlers.model.state.RobberPlaceState
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUIState }

/**
 * @author Vincent76;
 */
class RobberPlaceTUIState( nextState:State,
                           controller:Controller
                         ) extends RobberPlaceState( nextState, controller ) with TUIState{

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller, Robber, controller.game.onTurn )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.game.onTurn ) ) )
  }

  override def getActionInfo:String = "Select hex [<id>] for the robber"

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit = placeRobber( commandInput.input.toInt )
}
