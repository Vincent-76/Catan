package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.{ GUI, DisplayState, FieldInputDisplayState, GUIState }
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.Player
import de.htwg.se.catan.model.state.BuildInitRoadState

/**
 * @author Vincent76;
 */
case class BuildInitRoadGUIState( state:BuildInitRoadState, gui:GUI ) extends GUIState:

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( gui.game.getBuildableRoadSpotsForSettlement( state.settlementVID ) ) {
      override def action( id:Int ):Unit = gui.api.buildInitRoad( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( gui.game.player, false )
