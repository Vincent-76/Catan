package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.view.gui.{ DisplayState, FieldInputDisplayState, GUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Player
import com.aimit.htwg.catan.model.impl.placement.RoadPlacement

/**
 * @author Vincent76;
 */
case class DevRoadBuildingGUIState( controller:Controller ) extends GUIState {

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( RoadPlacement.getBuildablePoints( controller.game, controller.onTurn ) ) {
      override def action( id:Int ):Unit = controller.action( _.devBuildRoad( id ) )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
