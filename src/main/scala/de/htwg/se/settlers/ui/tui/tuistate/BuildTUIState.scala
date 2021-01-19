package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.StructurePlacement
import de.htwg.se.settlers.model.state.BuildState
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUIState }

/**
 * @author Vincent76;
 */
class BuildTUIState( structure:StructurePlacement,
                     controller:Controller
                   ) extends BuildState( structure, controller ) with TUIState {

  override def getGameDisplay:Option[String] = {
    Some( GameDisplay( controller, structure, controller.game.onTurn ).buildGameField )
  }

  override def getActionInfo:String = "Select position [<id>] for your " + structure.s

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit = build( commandInput.input.toInt )
}
