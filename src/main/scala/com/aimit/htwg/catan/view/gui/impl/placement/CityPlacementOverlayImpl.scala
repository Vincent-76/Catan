package com.aimit.htwg.catan.view.gui.impl.placement

import com.aimit.htwg.catan.view.gui.gamefield.GameFieldPane
import com.aimit.htwg.catan.model.{ Building, City }

object CityPlacementOverlayImpl extends VertexPlacementOverlayImpl[City] {

  val citySize:Double = 6

  protected def doDraw( building:Building ):Boolean = building.isInstanceOf[City]

  def points( hSize:Double, c:(Double, Double) ):List[(Double, Double)] = {
    val l = GameFieldPane.mult( citySize, hSize )
    List(
      (c._1 + 1.4 * l, c._2 + l),
      (c._1 - 1.4 * l, c._2 + l),
      (c._1 - 1.4 * l, c._2 - l),
      (c._1, c._2 - l),
      (c._1, c._2 - 2.5 * l),
      (c._1 + 0.7 * l, c._2 - 3 * l),
      (c._1 + 1.4 * l, c._2 - 2.5 * l)
    )
  }
}
