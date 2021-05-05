package de.htwg.se.settlers.aview.gui.commands

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.aview.gui.util.{ CustomDialog, ResourceSelector }
import de.htwg.se.settlers.aview.gui.{ GUI, GUIApp }
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.control.{ Button, ButtonType }
import scalafx.scene.layout.{ BorderPane, Region, VBox }
import scalafx.scene.text.{ Text, TextAlignment }

/**
 * @author Vincent76;
 */
abstract class TradeCommand( title:String ) extends SimpleGUICommand( title ) {

  override def action( gui:GUI ):Unit = new CustomDialog( gui, title, ButtonType.Cancel ) {
    headerText = "Select resources to trade"
    content = new VBox {
      spacing = 10
      val give = new ResourceSelector( gui.controller.player.resources )
      val get = new ResourceSelector()
      children = List(
        new VBox {
          alignment = Pos.Center
          children = List(
            new Text( "Give resources" ) {
              textAlignment = TextAlignment.Center
              underline = true
            },
            give
          )
        },
        new VBox {
          alignment = Pos.Center
          children = List(
            new Text( "Get resources" ) {
              textAlignment = TextAlignment.Center
              underline = true
            },
            get
          )
        },
        new BorderPane {
          right = new Button( "Trade" ) {
            onAction = _ => onTrade( gui, give.values, get.values )
          }
        }
      )
    }
  }.show()

  def onTrade( gui:GUI, give:ResourceCards, get:ResourceCards ):Unit
}
