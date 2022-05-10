package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.{ GUI, DisplayState, FieldInputDisplayState, GUIState }
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.Player
import de.htwg.se.catan.model.impl.placement.SettlementPlacement

/**
 * @author Vincent76;
 */
case class BuildInitSettlementGUIState( gui:GUI ) extends GUIState:

  override def getDisplayState:DisplayState =
    new FieldInputDisplayState( SettlementPlacement.getBuildablePoints( gui.game, gui.game.onTurn, any = true ) ) {
      override def action( id:Int ):Unit = gui.api.buildInitSettlement( id )
    }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( gui.game.player, false )
