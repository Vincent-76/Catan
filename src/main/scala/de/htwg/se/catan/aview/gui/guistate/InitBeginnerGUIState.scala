package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.{ GUI, DisplayState, GUIApp, GUIState, InitDisplayState }
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.PlayerID
import de.htwg.se.catan.model.state.InitBeginnerState
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{ GridPane, Pane, VBox }
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class InitBeginnerGUIState( state:InitBeginnerState, gui:GUI ) extends GUIState:
  override def getDisplayState:DisplayState = new InitDisplayState {
    override def getDisplayPane:Pane = new VBox {
      spacing = 10
      alignment = Pos.Center
      children = List(
        new GridPane {
          alignment = Pos.Center
          state.diceValues.filter( _._2 > 0 ).toList.zipWithIndex.foreach( d => {
            val p = gui.game.player( d._1._1 )
            add( new Text( p.name ) {
              fill = GUI.colorOf( p.color )
              //effect = new Glow( 1.0 )
              styleClass.add( "initBeginnerName" )
            }, 0, d._2 )
            add( new Text( d._1._2.toString ) {
              fill = Color.White
              margin = Insets( 0, 0, 0, 20 )
              style = "-fx-font-size: 20"
            }, 1, d._2 )
          } )
        }
      ) ++ (if state.beginner.isDefined then
        beginnerInfo( state.beginner.get )
      else
        (if state.diceValues.nonEmpty then
          List( new Text( "Tie, roll again." ) {
            fill = Color.White
            style = "-fx-font-size: 20"
          } )
        else Nil) :+ new Button( "Roll the dices" ) {
          styleClass.add( "button" )
          onAction = _ => gui.api.diceOutBeginner()
        }
      )
    }

    private def beginnerInfo( beginner:PlayerID ):List[Node] = List(
      new Text( gui.game.player( beginner ).name + " begins." ) {
        fill = Color.White //GUI.colorOf( controller.player( beginner ).color )
        style = "-fx-font-size: 20"
      },
      new Button( "Continue" ) {
        styleClass.add( "button" )
        onAction = _ => gui.api.setBeginner()
      }
    )
  }
