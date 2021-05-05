package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Player
import de.htwg.se.settlers.model.state.PlayerTradeState
import de.htwg.se.settlers.aview.gui.util.{ ActionHeader, FlowGridPane }
import de.htwg.se.settlers.aview.gui.{ GUI, GUICommand, GUIState }
import de.htwg.se.settlers.util._
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{ BorderPane, HBox, Priority, VBox }
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class PlayerTradeGUIState( state:PlayerTradeState, controller:Controller ) extends GUIState {

  override def getActions:List[GUICommand] = List( new GUICommand {
    override def getNode( gui:GUI ):Node = new BorderPane {
      vgrow = Priority.Always
      val p:Player = controller.player( state.pID )
      top = new ActionHeader( "Do you want to trade with " + controller.player.name + "?" )
      center = new VBox {
        spacing = 10
        alignment = Pos.Center
        hgrow = Priority.Always
        children = List(
          new Text( "Give" ),
          resourceDisplay( state.get ),
          new Text( "Get" ),
          resourceDisplay( state.give )
        )
      }
      bottom = new HBox {
        spacing = 10
        alignmentInParent = Pos.Center
        children = List(
          new Button( "Yes" ) {
            onAction = _ => gui.controller.game.state.playerTradeDecision( true )
          },
          new Button( "No" ) {
            onAction = _ => gui.controller.game.state.playerTradeDecision( false )
          }
        )
      }
    }
  } )

  private def resourceDisplay( resources:ResourceCards ):FlowGridPane = new FlowGridPane( 2 ) {
    hgap = 16
    vgap = 6
    addAll( resources.sort.filter( _._2 > 0 ).map( d => new BorderPane {
      left = new Text( d._1.title ) {
        style = "-fx-font-size: 10"
      }
      right = new Text( d._2.toString ) {
        style = "-fx-font-size: 10"
      }
    } ) )
  }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player( state.pID ), true )
}
