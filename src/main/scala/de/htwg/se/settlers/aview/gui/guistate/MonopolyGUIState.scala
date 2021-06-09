package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Resources
import de.htwg.se.settlers.aview.gui.{GUI, GUIApp, GUICommand, GUIState}
import de.htwg.se.settlers.model.player.Player
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.Label
import scalafx.scene.effect.Glow
import scalafx.scene.layout.{BorderPane, Pane, Priority, StackPane, VBox}
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class MonopolyGUIState( controller:Controller ) extends GUIState {

  override def getActions:List[GUICommand] = List( new GUICommand {
    override def getPane(gui:GUI ):Node = new BorderPane {
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
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
