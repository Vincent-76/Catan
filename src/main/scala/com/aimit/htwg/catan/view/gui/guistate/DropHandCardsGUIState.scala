package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.view.gui.util.ResourceSelector
import com.aimit.htwg.catan.view.gui.{ GUI, GUICommand, GUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.model.Player
import com.aimit.htwg.catan.model.state.DropHandCardsState
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{ BorderPane, Pane, Priority }
import scalafx.scene.paint.Color
import scalafx.scene.text.{ Text, TextAlignment }

/**
 * @author Vincent76;
 */
case class DropHandCardsGUIState( state:DropHandCardsState, controller:Controller ) extends GUIState {

  override def getActions:List[GUICommand] = List( new GUICommand() {
    override def getNode( gui:GUI ):Node = new BorderPane {
      vgrow = Priority.Always
      val p:Player = controller.player( state.pID )
      val selector:ResourceSelector = new ResourceSelector( p.resources, maxAmount = Some( p.resources.amount / 2 ) ) {
        vgap = 4
        hgap = 4
        alignmentInParent = Pos.Center
        margin = Insets( 6, 0, 6, 0 )
      }
      top = new Text( "Drop " + (p.resources.amount / 2) + " resources!" ) {
        fill = Color.White
        alignmentInParent = Pos.Center
        textAlignment = TextAlignment.Center
      }
      center = selector
      bottom = new Button( "Drop" ) {
        alignmentInParent = Pos.Center
        onAction = _ => controller.action( _.dropResourceCardsToRobber( selector.values ) )
      }
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player( state.pID ), true )
}
