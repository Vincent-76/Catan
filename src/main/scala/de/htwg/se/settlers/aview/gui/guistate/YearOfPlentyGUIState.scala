package de.htwg.se.settlers.aview.gui.guistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player
import de.htwg.se.settlers.model.Cards._
import de.htwg.se.settlers.aview.gui.util.ResourceSelector
import de.htwg.se.settlers.aview.gui.{ GUI, GUICommand, GUIState }
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.{ BorderPane, Priority }
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class YearOfPlentyGUIState( controller:Controller ) extends GUIState {

  override def getActions:List[GUICommand] = List( new GUICommand {
    override def getNode( gui:GUI ):Node = new BorderPane {
      vgrow = Priority.Always
      val p:Player = controller.player
      val selector:ResourceSelector = new ResourceSelector( maxAmount = Some( p.resources.amount / 2 ) ) {
        vgap = 4
        hgap = 4
        alignmentInParent = Pos.Center
        margin = Insets( 6, 0, 6, 0 )
      }
      top = new Text( "Select 2 resources" ) {
        alignmentInParent = Pos.Center
      }
      center = selector
      bottom = new Button( "OK" ) {
        alignmentInParent = Pos.Center
        onAction = _ => controller.yearOfPlentyAction( selector.values )
      }
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
