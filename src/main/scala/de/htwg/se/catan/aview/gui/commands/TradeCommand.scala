package de.htwg.se.catan.aview.gui.commands

import de.htwg.se.catan.aview.gui.GUI
import de.htwg.se.catan.aview.gui.util.{ CustomDialog, ResourceSelector }
import de.htwg.se.catan.model.Cards.ResourceCards
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.control.{ Button, ButtonType }
import scalafx.scene.layout.{ BorderPane, VBox }
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
              style = "-fx-font-size: 18"
              textAlignment = TextAlignment.Center
              underline = true
            },
            give
          )
        },
        new VBox {
          margin = Insets.apply( 16, 0, 0, 0 )
          alignment = Pos.Center
          children = List(
            new Text( "Get resources" ) {
              style = "-fx-font-size: 18"
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
