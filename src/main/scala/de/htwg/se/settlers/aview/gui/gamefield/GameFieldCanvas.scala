package de.htwg.se.settlers.aview.gui.gamefield

import de.htwg.se.settlers.aview.gui.gamefield.GameFieldPane.Coords
import de.htwg.se.settlers.model.GameField
import scalafx.scene.canvas.Canvas

/**
 * @author Vincent76;
 */

trait GameFieldCanvas[T <: GameField] extends Canvas {

  def update( gameField:GameField, hWidth:Double, hSize:Double ):Coords =
    doUpdate( gameField.asInstanceOf[T], hWidth, hSize )

  def doUpdate( gameField:T, hWidth:Double, hSize:Double ):Coords

}
