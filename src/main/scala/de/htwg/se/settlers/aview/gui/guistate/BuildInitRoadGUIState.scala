package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.aview.gui.{ DisplayState, FieldInputDisplayState, GUIState }
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player
import de.htwg.se.settlers.model.state.BuildInitRoadState

/**
 * @author Vincent76;
 */
case class BuildInitRoadGUIState( state:BuildInitRoadState, controller:Controller ) extends GUIState {

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( controller.game.getBuildableRoadSpotsForSettlement( state.settlementVID ) ) {
      override def action( id:Int ):Unit = controller.buildInitRoad( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, false )
}
