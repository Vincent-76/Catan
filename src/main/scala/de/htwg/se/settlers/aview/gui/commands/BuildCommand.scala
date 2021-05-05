package de.htwg.se.settlers.aview.gui.commands

import de.htwg.se.settlers.model.StructurePlacement
import de.htwg.se.settlers.aview.gui.GUI
import de.htwg.se.settlers.aview.gui.util.CustomDialog
import scalafx.geometry.Pos
import scalafx.scene.control.{ Button, ButtonType }
import scalafx.scene.layout.HBox

/**
 * @author Vincent76;
 */
case object BuildCommand extends SimpleGUICommand( "Build" ) {

  override def action( gui:GUI ):Unit = new CustomDialog( gui, "Build", ButtonType.Cancel ) {
    headerText = "Choose a structure to build"
    content = new HBox {
      spacing = 10
      alignment = Pos.Center
      children = StructurePlacement.get.map( s => new Button( s.title ) {
        onAction = _ => gui.controller.game.state.setBuildState( s )
      } )
    }
  }.show()
}
