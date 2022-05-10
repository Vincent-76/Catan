package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.util.ActionHeader
import de.htwg.se.catan.aview.gui.{ GUI, GUICommand, GUIState }
import de.htwg.se.catan.model.Player
import de.htwg.se.catan.model.state.RobberStealState
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.{ BorderPane, Priority, VBox }

/**
 * @author Vincent76;
 */
case class RobberStealGUIState( state:RobberStealState, gui:GUI ) extends GUIState:
  override def getActions:List[GUICommand] = List( _ => new BorderPane {
    vgrow = Priority.Always
    top = new ActionHeader( "Select a player to steal from" )
    center = new VBox {
      spacing = 8
      alignment = Pos.Center
      children = state.adjacentPlayers.map( pID => new Button( gui.game.player( pID ).name ) {
        onAction = _ => gui.api.robberStealFromPlayer( pID )
      } )
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( gui.game.player, true )