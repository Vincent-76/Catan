package com.aimit.htwg.catan.view.gui

import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.layout.{ AnchorPane, Pane, Priority, VBox }

/**
 * @author Vincent76;
 */
class ActionPane( gui:GUI ) extends VBox {
  background = GUIApp.stoneBackground
  hgrow = Priority.Always
  vgrow = Priority.Always
  padding = Insets( 10 )
  fillWidth = true
  spacing = 10

  def update( commands:List[GUICommand] ):Unit = {
    children = commands.map( s => new AnchorPane {
      maxWidth = ActionPane.this.width.value - 20
      hgrow = Priority.Always
      vgrow = Priority.Always
      val node:Node = s.getNode( gui )
      children.add( node )
      AnchorPane.setAnchors( node, 0, 0, 0, 0 )
    } )

  }
}
