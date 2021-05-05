package de.htwg.se.settlers.aview.gui.util

import scalafx.geometry.Pos
import scalafx.scene.text.{ Text, TextAlignment }

/**
 * @author Vincent76;
 */
class ActionHeader( title:String ) extends Text( title ) {
  alignmentInParent = Pos.Center
  wrappingWidth = 120
  textAlignment = TextAlignment.Center
}
