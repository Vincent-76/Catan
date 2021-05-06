package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Player, Road }
import de.htwg.se.settlers.aview.gui.{ DisplayState, FieldInputDisplayState, GUIState }

/**
 * @author Vincent76;
 */
case class DevRoadBuildingGUIState( controller:Controller ) extends GUIState {

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( Road.getBuildablePoints( controller.game, controller.onTurn ) ) {
      override def action( id:Int ):Unit = controller.devBuildRoad( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
