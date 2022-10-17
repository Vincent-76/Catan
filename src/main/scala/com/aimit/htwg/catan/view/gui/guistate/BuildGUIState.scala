package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.state.BuildState
import com.aimit.htwg.catan.view.gui.{ DisplayState, FieldInputDisplayState, GUIState }
import com.aimit.htwg.catan.model.Player

/**
 * @author Vincent76;
 */
case class BuildGUIState( state:BuildState, controller:Controller ) extends GUIState {

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( state.structure.getBuildablePoints( controller.game, controller.onTurn ) ) {
      override def action( id:Int ):Unit = controller.build( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
