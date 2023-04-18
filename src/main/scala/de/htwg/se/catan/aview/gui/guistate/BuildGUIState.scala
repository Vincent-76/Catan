package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.state.BuildState
import de.htwg.se.catan.aview.gui.{ GUI, DisplayState, FieldInputDisplayState, GUIState }
import de.htwg.se.catan.model.Player

/**
 * @author Vincent76;
 */
case class BuildGUIState( state:BuildState, gui:GUI ) extends GUIState:

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( state.structure.getBuildablePoints( gui.game, gui.game.onTurn ) ) {
      override def action( id:Int ):Unit = gui.api.build( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( gui.game.player, true )
