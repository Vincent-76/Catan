package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Settlement
import de.htwg.se.settlers.aview.gui.{DisplayState, FieldInputDisplayState, GUIState}
import de.htwg.se.settlers.model.player.Player

/**
 * @author Vincent76;
 */
case class BuildInitSettlementGUIState( controller:Controller ) extends GUIState {

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( Settlement.getBuildablePoints( controller.game, controller.onTurn, any = true ) ) {
      override def action( id:Int ):Unit = controller.buildInitSettlement( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, false )
}
