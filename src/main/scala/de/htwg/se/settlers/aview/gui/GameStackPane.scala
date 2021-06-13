package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.model._
import scalafx.scene.layout._

/**
 * @author Vincent76;
 */
trait GameStackPane[T <: Game] extends VBox {

  def update( game:Game ):Unit = doUpdate( game.asInstanceOf[T] )

  def doUpdate( game:T ):Unit
}
