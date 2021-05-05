package de.htwg.se.settlers.aview.gui.commands

import de.htwg.se.settlers.aview.gui.{ GUIApp, GUI, GUICommand }
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.layout.Priority
import scalafx.scene.text.TextAlignment

/**
 * @author Vincent76;
 */
abstract class SimpleGUICommand( val text:String ) extends GUICommand {
  protected def action( gui:GUI ):Unit

  override def getNode( gui:GUI ):Node = new Button( text ) {
    onAction = _ => action( gui )
    alignment = Pos.Center
    textAlignment = TextAlignment.Center
    hgrow = Priority.Always
    vgrow = Priority.Always
    wrapText = true
  }
}