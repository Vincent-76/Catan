package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.view.gui.{ DisplayState, FieldInputDisplayState, GUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Player
import com.aimit.htwg.catan.model.state.BuildInitRoadState

/**
 * @author Vincent76;
 */
case class BuildInitRoadGUIState( state:BuildInitRoadState, controller:Controller ) extends GUIState {

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( controller.game.getBuildableRoadSpotsForSettlement( state.settlementVID ) ) {
      override def action( id:Int ):Unit = controller.action( _.buildInitRoad( id ) )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, false )
}
