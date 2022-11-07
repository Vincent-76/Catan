package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.view.gui.{ DisplayState, FieldInputDisplayState, GUIState }
import com.aimit.htwg.catan.model.Player
import com.aimit.htwg.catan.model.impl.placement.RobberPlacement

/**
 * @author Vincent76;
 */
case class RobberPlaceGUIState( controller:Controller ) extends GUIState {
  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( RobberPlacement.getBuildablePoints( controller.game, controller.onTurn ) ) {
      override def action( id:Int ):Unit = controller.action( _.placeRobber( id ) )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
