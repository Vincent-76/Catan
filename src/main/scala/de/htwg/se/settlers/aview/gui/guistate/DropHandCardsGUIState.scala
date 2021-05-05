package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player
import de.htwg.se.settlers.model.state.DropHandCardsState
import de.htwg.se.settlers.aview.gui.util.ResourceSelector
import de.htwg.se.settlers.aview.gui.{ GUI, GUICommand, GUIState }
import de.htwg.se.settlers.util._
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{ BorderPane, Priority }
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
      top = new Text( "Drop " + ( p.resources.amount / 2 ) + " resources!" ) {
        alignmentInParent = Pos.Center
        textAlignment = TextAlignment.Center
      }
      center = selector
      bottom = new Button( "Drop" ) {
        alignmentInParent = Pos.Center
        onAction = _ => controller.game.state.dropResourceCardsToRobber( selector.values )
      }
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player( state.pID ), true )
}
