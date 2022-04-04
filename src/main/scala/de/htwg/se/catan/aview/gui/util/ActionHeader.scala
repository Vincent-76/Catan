package de.htwg.se.catan.aview.gui.util

import scalafx.geometry.Pos
import scalafx.scene.paint.Color
import scalafx.scene.text.{ Text, TextAlignment }

/**
 * @author Vincent76;
 */
class ActionHeader( title:String ) extends Text( title ):
  alignmentInParent = Pos.Center
  wrappingWidth = 140
  textAlignment = TextAlignment.Center
  fill = Color.White
  style = "-fx-font-size: 16; -fx-font-weight: bold;"