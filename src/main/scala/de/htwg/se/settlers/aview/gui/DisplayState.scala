package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.model.PlacementPoint
import scalafx.scene.Node
import scalafx.scene.layout.Pane

/**
 * @author Vincent76;
 */
sealed trait DisplayState

trait InitDisplayState extends DisplayState {
  def getDisplayPane:Pane


}

trait FieldDisplayState extends DisplayState

case object FieldDisplayState extends FieldDisplayState

abstract class FieldInputDisplayState( val points:List[PlacementPoint] ) extends FieldDisplayState {
  def action( id:Int ):Unit
}
