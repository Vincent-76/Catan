package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.{ GUICommand, GUIState }
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.{ Player, Resources }
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.control.Label
import scalafx.scene.effect.Glow
import scalafx.scene.layout.{ BorderPane, Priority, StackPane, VBox }
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class MonopolyGUIState( controller:Controller ) extends GUIState {

  override def getActions:List[GUICommand] = List( _ => new BorderPane {
    vgrow = Priority.Always
    top = new Text( "Specify a resource to get all the corresponding cards from the other players" ) {
      alignmentInParent = Pos.Center
      wrappingWidth = 120
    }
    center = new VBox {
      alignmentInParent = Pos.Center
      alignment = Pos.Center
      vgrow = Priority.Always
      spacing = 10
      children = Resources.get.map( r => new StackPane {
        style = "-fx-font-size: 12; -fx-border-color: #353535; -fx-background-color: #FFFFFF; -fx-cursor: hand"
        padding = Insets( 2 )
        minWidth = 60
        children = new Label( r.title )
        onMouseEntered = _ => effect = new Glow( 0.7 )
        onMouseExited = _ => effect = null
        onMouseClicked = _ => controller.monopolyAction( r )
      } )
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
