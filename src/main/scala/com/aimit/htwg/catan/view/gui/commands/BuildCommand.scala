package com.aimit.htwg.catan.view.gui.commands

import com.aimit.htwg.catan.view.gui.GUI
import com.aimit.htwg.catan.view.gui.util.CustomDialog
import com.aimit.htwg.catan.model.StructurePlacement
import com.aimit.htwg.catan.util.RichIterable
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
      children = gui.controller.game.availablePlacements.withType[StructurePlacement].map( s => new Button( s.title ) {
        onAction = _ => gui.controller.setBuildState( s )
      } )
    }
  }.show()
}
