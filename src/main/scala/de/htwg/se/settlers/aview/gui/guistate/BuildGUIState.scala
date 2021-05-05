package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player
import de.htwg.se.settlers.model.state.BuildState
import de.htwg.se.settlers.aview.gui.{ DisplayState, FieldInputDisplayState, GUIState }

/**
 * @author Vincent76;
 */
case class BuildGUIState( state:BuildState, controller:Controller ) extends GUIState {

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( state.structure.getBuildablePoints( controller.game, controller.onTurn ) ) {
      override def action( id:Int ):Unit = controller.game.state.build( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
