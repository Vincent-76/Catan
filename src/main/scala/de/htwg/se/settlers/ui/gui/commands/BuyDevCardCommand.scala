package de.htwg.se.settlers.ui.gui.commands

import de.htwg.se.settlers.ui.gui.GUI
import de.htwg.se.settlers.ui.gui.util.CustomDialog
import scalafx.geometry.Pos
import scalafx.scene.control.{ Button, ButtonType }
import scalafx.scene.layout.VBox

/**
 * @author Vincent76;
 */
case object BuyDevCardCommand extends SimpleGUICommand( "Buy Development Card" ) {

  override protected def action( gui:GUI ):Unit =
    new CustomDialog( gui, "Buy a Development Card", ButtonType.Cancel ) {
      headerText = "Do you want to buy a development card?"
      content = new VBox {
        spacing = 10
        children = new Button( "Buy" ) {
          alignmentInParent = Pos.CenterRight
          onAction = _ => gui.controller.game.state.buyDevCard()
        }
      }
    }.show()
}
