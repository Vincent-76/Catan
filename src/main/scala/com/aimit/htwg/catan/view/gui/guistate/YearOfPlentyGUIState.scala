package com.aimit.htwg.catan.view.gui.guistate

import com.aimit.htwg.catan.view.gui.util.ResourceSelector
import com.aimit.htwg.catan.view.gui.{ GUICommand, GUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.model.Player
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.control.Button
import scalafx.scene.layout.{ BorderPane, Priority }
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class YearOfPlentyGUIState( controller:Controller ) extends GUIState {

  override def getActions:List[GUICommand] = List( _ => new BorderPane {
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
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( controller.player, true )
}
