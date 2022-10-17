package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.view.gui.{ DisplayState, GUIState, InitDisplayState }
import com.aimit.htwg.catan.controller.Controller
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.{ Pane, VBox }
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
          styleClass.add( "initHeader" )
        },
        new Button( "Add Players" ) {
          onAction = _ => controller.initGame()
          styleClass.add( "button" )
        }
      )
    }
  }
}
