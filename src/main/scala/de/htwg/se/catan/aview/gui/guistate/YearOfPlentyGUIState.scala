package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.util.ResourceSelector
import de.htwg.se.catan.aview.gui.{ GUI, GUICommand, GUIState }
import de.htwg.se.catan.model.Card._
import de.htwg.se.catan.model.Player
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.control.Button
import scalafx.scene.layout.{ BorderPane, Priority }
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class YearOfPlentyGUIState( gui:GUI ) extends GUIState:

  override def getActions:List[GUICommand] = List( _ => new BorderPane {
    vgrow = Priority.Always
    val p:Player = gui.game.player
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
      onAction = _ => gui.api.yearOfPlentyAction( selector.values )
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( gui.game.player, true )