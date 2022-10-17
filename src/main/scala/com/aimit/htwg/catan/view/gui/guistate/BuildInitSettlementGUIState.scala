package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.view.gui.{ DisplayState, FieldInputDisplayState, GUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Player
import com.aimit.htwg.catan.model.impl.placement.SettlementPlacement

/**
 * @author Vincent76;
 */
case class BuildInitSettlementGUIState( controller:Controller ) extends GUIState {

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( SettlementPlacement.getBuildablePoints( controller.game, controller.onTurn, any = true ) ) {
      override def action( id:Int ):Unit = controller.buildInitSettlement( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, false )
}
