package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.aview.gui.{ DisplayState, FieldInputDisplayState, GUIState }
import de.htwg.se.catan.model.Player
import de.htwg.se.catan.model.impl.placement.RobberPlacement

/**
 * @author Vincent76;
 */
case class RobberPlaceGUIState( controller:Controller ) extends GUIState:
  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( RobberPlacement.getBuildablePoints( controller.game, controller.onTurn ) ) {
      override def action( id:Int ):Unit = controller.placeRobber( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )