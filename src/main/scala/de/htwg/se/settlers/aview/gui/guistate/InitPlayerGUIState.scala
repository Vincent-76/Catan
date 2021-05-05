package de.htwg.se.settlers.aview.gui.guistate

import scalafx.Includes._
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player
import de.htwg.se.settlers.aview.gui.{ DisplayState, FieldDisplayState, GUIApp, GUIState, InitDisplayState }
import javafx.scene.input.KeyCode
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.{ Button, ComboBox, TextField }
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.{ HBox, VBox }
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class InitPlayerGUIState( controller:Controller ) extends GUIState {
  override def getDisplayState:DisplayState = new InitDisplayState {
    override def getDisplayNode:Node = new VBox {
      spacing = 10
      alignment = Pos.Center
      val textField:TextField = new TextField {
        maxWidth = 80
        promptText = "Name"
        alignment = Pos.Center
      }
      val comboBox:ComboBox[String] = new ComboBox[String]( Player.availableColors( controller.game.players.values ).map( _.name ) ) {
        selectionModel.value.selectFirst()
      }
      onKeyPressed = ( e:KeyEvent ) => {
        if ( e.getCode == KeyCode.ENTER )
          controller.game.state.addPlayer( Player.colorOf( comboBox.getValue ).get, textField.getText() )
      }
      children = List(
        new Text( "Enter a name and choose a color to add a player,\n or continue to dice out the beginner." ),
        textField,
        comboBox,
        new HBox {
          spacing = 10
          alignment = Pos.Center
          children = List(
            new Button( "Add Player" ) {
              onAction = _ => controller.game.state.addPlayer( Player.colorOf( comboBox.getValue ).get, textField.getText() )
            },
            new Button( "Continue" ) {
              onAction = _ => {
                if( textField.getText.nonEmpty )
                  controller.game.state.addPlayer( Player.colorOf( comboBox.getValue ).get, textField.getText() )
                controller.game.state.setInitBeginnerState()
              }
            }
          )
        }
      )
    }
  }
}
