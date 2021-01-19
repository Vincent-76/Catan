package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Road, State }
import de.htwg.se.settlers.model.state.DevRoadBuildingState
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUIState }

/**
 * @author Vincent76;
 */
class DevRoadBuildingTUIState( nextState:State,
                               roads:Int,
                               controller: Controller
                             ) extends DevRoadBuildingState( nextState, roads, controller ) with TUIState {

  override def getGameDisplay:Option[String] = Some( GameDisplay( controller, Road, controller.game.onTurn ).buildGameField )

  override def getActionInfo:String = "Select position [<id>] for your road"

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit = devBuildRoad( commandInput.input.toInt )
}
