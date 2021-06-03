package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.aview.gui.{DisplayState, GUIApp, GUIState, InitDisplayState}
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{Pane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class InitGUIState( controller:Controller ) extends GUIState {
  override def getDisplayState:DisplayState = new InitDisplayState {
    override def getDisplayPane:Pane = new VBox {
      spacing = 10
      alignment = Pos.Center
      children = List(
        new Text( "Welcome to the Settlers of Catan" ) {
          fill = Color.White
        },
        new Button( "Add Players" ) {
          onAction = _ => controller.initPlayers()
        }
      )
    }
  }
}
