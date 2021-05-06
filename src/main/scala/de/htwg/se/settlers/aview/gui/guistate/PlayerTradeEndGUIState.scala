package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player
import de.htwg.se.settlers.model.state.PlayerTradeEndState
import de.htwg.se.settlers.aview.gui.{ GUI, GUICommand, GUIState, GUIApp }
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{ BorderPane, Priority, VBox }
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class PlayerTradeEndGUIState( state:PlayerTradeEndState, controller:Controller ) extends GUIState {

  override def getActions:List[GUICommand] = List( new GUICommand {
    override def getNode( gui:GUI ):Node = new BorderPane {
      vgrow = Priority.Always
      top = new Text( "Choose trade player" ) {
        alignmentInParent = Pos.Center
      }
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
            },
            new Button( "Trade" ) {
              onAction = _ => controller.playerTrade( d._1 )
            }
          )
        } )
      }
      bottom = new Button( "Abort" ) {
        alignmentInParent = Pos.Center
        onAction = _ => controller.abortPlayerTrade()
      }
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
