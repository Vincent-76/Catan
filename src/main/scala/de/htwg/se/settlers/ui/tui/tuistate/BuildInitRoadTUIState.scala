package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Road
import de.htwg.se.settlers.model.state.BuildInitRoadState
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI, TUIState }

/**
 * @author Vincent76;
 */
class BuildInitRoadTUIState( vID:Int, controller:Controller
                           ) extends BuildInitRoadState( vID, controller ) with TUIState {

  override def getGameDisplay:Option[String] = {
    Some( GameDisplay( controller, Road, controller.game.getBuildableRoadSpotsForSettlement( vID ) ).buildGameField )
  }

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player ) + " place your road." )
    "Select position [<id>] for your road"
  }

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit = buildInitRoad( commandInput.command.get.toInt )
}
