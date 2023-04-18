package de.htwg.se.catan.aview.gui.util

import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.effect.Glow
import scalafx.scene.layout.StackPane

/**
 * @author Vincent76;
 */
class GlowButton( title:String ) extends StackPane:
  styleClass.add( "glowButton" )
  padding = Insets( 2 )
  children = new Label( title ) {
    styleClass.add( "glowButtonLabel" )
  }
  onMouseEntered = _ => effect = new Glow( 0.7 )
  onMouseExited = _ => effect = null
