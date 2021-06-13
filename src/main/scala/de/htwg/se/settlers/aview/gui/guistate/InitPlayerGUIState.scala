package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.aview.gui.{ DisplayState, GUIState, InitDisplayState }
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.PlayerColor
import javafx.scene.input.KeyCode
import scalafx.Includes._
import scalafx.geometry.Pos
import scalafx.scene.control.{ Button, ComboBox, TextField }
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.{ HBox, Pane, VBox }
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class InitPlayerGUIState( controller:Controller ) extends GUIState {
  override def getDisplayState:DisplayState = new InitDisplayState {
    override def getDisplayPane:Pane = new VBox {
      spacing = 10
      alignment = Pos.Center
      val textField:TextField = new TextField {
        styleClass.add( "addPlayerInput" )
        maxWidth = 200
        promptText = "Name"
        alignment = Pos.Center
      }
      val comboBox:ComboBox[String] = new ComboBox[String]( PlayerColor.availableColors( controller.game.players.values.map( _.color ) ).map( _.name ) ) {
        selectionModel.value.selectFirst()
        styleClass.add( "button" )
      }
      onKeyPressed = ( e:KeyEvent ) => {
        if( e.getCode == KeyCode.ENTER )
          controller.addPlayer( PlayerColor.colorOf( comboBox.getValue ).get, textField.getText() )
      }
      children = List(
        new Text( "Enter a name and choose a color to add a player,\n or continue to dice out the beginner." ) {
          fill = Color.White
          style = "-fx-font-size: 20;"
        },
        textField,
        comboBox,
        new HBox {
          spacing = 10
          alignment = Pos.Center
          children = List(
            new Button( "Add Player" ) {
              styleClass.add( "button" )
              onAction = _ => controller.addPlayer( PlayerColor.colorOf( comboBox.getValue ).get, textField.getText() )
            },
            new Button( "Continue" ) {
              styleClass.add( "button" )
              onAction = _ => {
                if( textField.getText.nonEmpty )
                  controller.addPlayer( PlayerColor.colorOf( comboBox.getValue ).get, textField.getText() )
                controller.setInitBeginnerState()
              }
            }
          )
        }
      )
    }
  }
}
