package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.view.gui.util.ActionHeader
import com.aimit.htwg.catan.view.gui.{ GUICommand, GUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Player
import com.aimit.htwg.catan.model.state.RobberStealState
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.{ BorderPane, Priority, VBox }

/**
 * @author Vincent76;
 */
case class RobberStealGUIState( state:RobberStealState, controller:Controller ) extends GUIState {
  override def getActions:List[GUICommand] = List( _ => new BorderPane {
    vgrow = Priority.Always
    top = new ActionHeader( "Select a player to steal from" )
    center = new VBox {
      spacing = 8
      alignment = Pos.Center
      children = state.adjacentPlayers.map( pID => new Button( controller.player( pID ).name ) {
        onAction = _ => controller.action( _.robberStealFromPlayer( pID ) )
      } )
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
