package de.htwg.se.catan.aview.gui.gamefield

import de.htwg.se.catan.aview.gui.gamefield.GameFieldPane.Coords
import de.htwg.se.catan.aview.gui.impl.gamefield.ClassicGameFieldCanvasImpl
import de.htwg.se.catan.model.GameField
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import scalafx.scene.canvas.Canvas

/**
 * @author Vincent76;
 */

object GameFieldCanvas:
  def get( gameField:GameField ):GameFieldCanvas[_] = gameField  match
    case _:ClassicGameFieldImpl => new ClassicGameFieldCanvasImpl()
    case c => throw new NotImplementedError( "GameFieldCanvas[" + c.getClass.getName + "]" )

trait GameFieldCanvas[T <: GameField] extends Canvas:

  def update( gameField:GameField, hWidth:Double, hSize:Double ):Coords =
    doUpdate( gameField.asInstanceOf[T], hWidth, hSize )

  protected def doUpdate( gameField:T, hWidth:Double, hSize:Double ):Coords
