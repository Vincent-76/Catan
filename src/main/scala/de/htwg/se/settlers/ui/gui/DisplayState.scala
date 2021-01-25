package de.htwg.se.settlers.ui.gui

import de.htwg.se.settlers.model.GameField.PlacementPoint
import scalafx.scene.Node

/**
 * @author Vincent76;
 */
sealed trait DisplayState

trait InitDisplayState extends DisplayState {
  def getDisplayNode:Node


}

trait FieldDisplayState extends DisplayState

case object FieldDisplayState extends FieldDisplayState

abstract class FieldInputDisplayState( val points:List[PlacementPoint] ) extends FieldDisplayState {
  def action( id:Int ):Unit
}
