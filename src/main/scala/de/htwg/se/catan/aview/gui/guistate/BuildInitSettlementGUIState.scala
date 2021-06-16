package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.{ DisplayState, FieldInputDisplayState, GUIState }
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.Player
import de.htwg.se.catan.model.impl.placement.SettlementPlacement

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
