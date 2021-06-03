package de.htwg.se.settlers.aview.gui.util

import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.effect.Glow
import scalafx.scene.layout.StackPane

/**
 * @author Vincent76;
 */
class GlowButton( title:String ) extends StackPane {
  style = "-fx-font-size: 9; -fx-border-color: #202020; -fx-background-color: #5a5a5a; -fx-cursor: hand"
  padding = Insets( 2 )
  children = new Label( title )
  onMouseEntered = _ => effect = new Glow( 0.7 )
  onMouseExited = _ => effect = null
}
