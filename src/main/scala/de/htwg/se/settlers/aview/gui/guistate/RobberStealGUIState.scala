package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.state.RobberStealState
import de.htwg.se.settlers.aview.gui.util.ActionHeader
import de.htwg.se.settlers.aview.gui.{GUI, GUICommand, GUIState}
import de.htwg.se.settlers.model.player.Player
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, Pane, Priority, VBox}

/**
 * @author Vincent76;
 */
case class RobberStealGUIState( state:RobberStealState, controller:Controller ) extends GUIState {
  override def getActions:List[GUICommand] = List( new GUICommand {
    override def getPane(gui:GUI ):Node = new BorderPane {
      vgrow = Priority.Always
      top = new ActionHeader( "Select a player to steal from" )
      center = new VBox {
        spacing = 8
        alignment = Pos.Center
        children = state.adjacentPlayers.map( pID => new Button( controller.player( pID ).name ) {
          onAction = _ => controller.robberStealFromPlayer( pID )
        } )
      }
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
