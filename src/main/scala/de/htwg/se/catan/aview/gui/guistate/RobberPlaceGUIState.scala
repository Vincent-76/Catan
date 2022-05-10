package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.{ GUI, DisplayState, FieldInputDisplayState, GUIState }
import de.htwg.se.catan.model.Player
import de.htwg.se.catan.model.impl.placement.RobberPlacement

/**
 * @author Vincent76;
 */
case class RobberPlaceGUIState( gui:GUI ) extends GUIState:
  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( RobberPlacement.getBuildablePoints( gui.game, gui.game.onTurn ) ) {
      override def action( id:Int ):Unit = gui.api.placeRobber( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( gui.game.player, true )