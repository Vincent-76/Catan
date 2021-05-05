package de.htwg.se.settlers.aview.gui.commands

import de.htwg.se.settlers.model.Cards
import de.htwg.se.settlers.aview.gui.GUI
import de.htwg.se.settlers.aview.gui.util.CustomDialog
import de.htwg.se.settlers.util._
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.{ Button, ButtonType }
import scalafx.scene.layout.{ HBox, Priority, VBox }

/**
 * @author Vincent76;
 */
case object UseDevCardCommand extends SimpleGUICommand( "Use Development Card" ) {

  override protected def action( gui:GUI ):Unit =
    new CustomDialog( gui, "Use a Development Card", ButtonType.Cancel ) {
    headerText = "Choose the development card you want to use"
    content = new VBox {
      spacing = 10
      alignment = Pos.Center
      children = gui.controller.player.devCards.filter( _.usable ).sortBySeq( Cards.devCards ).grouped( 4 ).toList.map( d => {
        new HBox {
          spacing = 10
          alignment = Pos.Center
          children = d.map( d => new Button( d.title ) {
            onAction = _ => gui.controller.game.state.useDevCard( d )
          } )
        }
      } )
    }
  }.show()

  override def getNode( gui:GUI ):Node = super.getNode( gui ).use( n => {
    n.disable = !gui.controller.player.devCards.exists( _.usable )
    n
  } )
}
