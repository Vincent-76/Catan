package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.view.gui.util.ActionHeader
import com.aimit.htwg.catan.view.gui.{ GUIApp, GUICommand, GUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Player
import com.aimit.htwg.catan.model.state.PlayerTradeEndState
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.{ BorderPane, Priority, VBox }
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class PlayerTradeEndGUIState( state:PlayerTradeEndState, controller:Controller ) extends GUIState {

  override def getActions:List[GUICommand] = List( _ => new BorderPane {
    vgrow = Priority.Always
    top = new ActionHeader( "Choose trade player" )
    center = new VBox {
      spacing = 8
      alignmentInParent = Pos.Center
      alignment = Pos.Center
      children = state.decisions.filter( _._2 ).map( d => new VBox {
        spacing = 4
        alignment = Pos.Center
        children = List(
          new Text( controller.player( d._1 ).name ) {
            fill = GUIApp.colorOf( controller.player( d._1 ).color )
            styleClass.add( "playerInfoName" )
            style = "-fx-font-size: 16;"
          },
          new Button( "Trade" ) {
            styleClass.add( "button" )
            onAction = _ => controller.playerTrade( d._1 )
          }
        )
      } )
    }
    bottom = new Button( "Abort" ) {
      alignmentInParent = Pos.Center
      onAction = _ => controller.abortPlayerTrade()
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
