package com.aimit.htwg.catan.view.gui.impl.placement

import com.aimit.htwg.catan.view.gui.gamefield.GameFieldPane.Coords
import com.aimit.htwg.catan.view.gui.gamefield.{ GameFieldPane, PlacementOverlay }
import com.aimit.htwg.catan.model.Game
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

object RobberPlacementOverlayImpl extends PlacementOverlay {

  val robberDist:Double = 0.5
  val robberSize:Double = 6

  def draw( game:Game, context:GraphicsContext, coords:Coords, hSize:Double ):Unit = {
    val c = coords( game.gameField.robberHex )
    val points = getRobberCoordinates( hSize, (c._1, c._2 - robberDist * hSize) )
    context.fill = Color.DarkSlateGray
    context.fillPolygon( points )
    context.stroke = Color.Black
    context.lineWidth = 1
    context.strokePolygon( points )
  }

  private def getRobberCoordinates( hSize:Double, p:(Double, Double) ):List[(Double, Double)] = {
    val l = GameFieldPane.mult( robberSize, hSize )
    List(
      (p._1 + l, p._2 + l),
      (p._1 - l, p._2 + l),
      (p._1 - l, p._2 - l),
      (p._1 - l / 2, p._2 - l),
      (p._1 - l / 2, p._2 - 1.5 * l),
      (p._1 + l / 2, p._2 - 1.5 * l),
      (p._1 + l / 2, p._2 - l),
      (p._1 + l, p._2 - l)
    )
  }
}
