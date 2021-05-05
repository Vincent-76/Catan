package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.state.InitBeginnerState
import de.htwg.se.settlers.aview.gui.{ DisplayState, GUIApp, GUIState, InitDisplayState }
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{ GridPane, VBox }
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class InitBeginnerGUIState( state:InitBeginnerState, controller:Controller ) extends GUIState {
  override def getDisplayState:DisplayState = new InitDisplayState {
    override def getDisplayNode:Node = new VBox {
      spacing = 10
      alignment = Pos.Center
      children = List(
        new GridPane {
          alignment = Pos.Center
          state.diceValues.filter( _._2 > 0 ).toList.zipWithIndex.foreach( d => {
            val p = controller.player( d._1._1 )
            add( new Text( p.name ) {
              fill = GUIApp.colorOf( p.color )
              style = "-fx-font-size: 16"
            }, 0, d._2 )
            add( new Text( d._1._2.toString ){
              margin = Insets( 0, 0, 0, 20 )
              style = "-fx-font-size: 16"
            }, 1, d._2 )
          } )
        }
      ) ++ ( if ( state.beginner.isDefined )
        beginnerInfo( state.beginner.get )
      else {
        ( if ( state.diceValues.nonEmpty )
          List( new Text( "Tie, roll again." ) {
            style = "-fx-font-size: 16"
          } )
        else Nil ) :+ new Button( "Roll the dices" ) {
          onAction = _ => state.diceOutBeginner()
        }
      } )
    }

    private def beginnerInfo( beginner:PlayerID ):List[Node] = List(
      new Text( controller.player( beginner ).name + " begins." ) {
        fill = GUIApp.colorOf( controller.player( beginner ).color )
        style = "-fx-font-size: 16"
      },
      new Button( "Continue" ) {
        onAction = _ => controller.game.state.setBeginner()
      }
    )
  }
}
