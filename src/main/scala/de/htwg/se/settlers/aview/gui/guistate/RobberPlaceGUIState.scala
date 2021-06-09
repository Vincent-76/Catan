package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Robber
import de.htwg.se.settlers.aview.gui.{DisplayState, FieldInputDisplayState, GUIState}
import de.htwg.se.settlers.model.player.Player

/**
 * @author Vincent76;
 */
case class RobberPlaceGUIState( controller:Controller ) extends GUIState {
  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( Robber.getBuildablePoints( controller.game, controller.onTurn ) ) {
      override def action( id:Int ):Unit = controller.placeRobber( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
