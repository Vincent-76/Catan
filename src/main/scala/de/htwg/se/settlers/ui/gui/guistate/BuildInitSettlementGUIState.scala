package de.htwg.se.settlers.ui.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Player, Settlement }
import de.htwg.se.settlers.ui.gui.{ DisplayState, FieldInputDisplayState, GUIState }

/**
 * @author Vincent76;
 */
case class BuildInitSettlementGUIState( controller:Controller ) extends GUIState {

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( Settlement.getBuildablePoints( controller.game, controller.onTurn, any = true ) ) {
      override def action( id:Int ):Unit = controller.game.state.buildInitSettlement( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, false )
}
