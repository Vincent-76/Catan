package de.htwg.se.catan.aview.gui.commands

import de.htwg.se.catan.aview.gui.GUI
import de.htwg.se.catan.aview.gui.util.CustomDialog
import de.htwg.se.catan.model.{ Card, DevelopmentCard }
import de.htwg.se.catan.util._
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.{ Button, ButtonType }
import scalafx.scene.layout.{ HBox, Pane, VBox }

/**
 * @author Vincent76;
 */
case object UseDevCardCommand extends SimpleGUICommand( "Use Development Card" ):

  override protected def action( gui:GUI ):Unit =
    new CustomDialog( gui, "Use a Development Card", ButtonType.Cancel ) {
      headerText = "Choose the development card you want to use"
      content = new VBox {
        spacing = 10
        alignment = Pos.Center
        children = gui.game.player.devCards.filter( _.usable ).sortBy( _.title ).grouped( 4 ).toList.map( d => {
          new HBox {
            spacing = 10
            alignment = Pos.Center
            children = d.map( d => new Button( d.title ) {
              onAction = _ => gui.api.useDevCard( d )
            } )
          }
        } )
      }
    }.show()

  override def getNode( gui:GUI ):Node = super.getNode( gui ).use( n => {
    n.disable = !gui.game.player.devCards.exists( _.usable )
    n
  } )
