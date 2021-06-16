package de.htwg.se.catan.aview.gui.impl.placement

import de.htwg.se.catan.aview.gui.gamefield.GameFieldPane
import de.htwg.se.catan.model.{ Building, Settlement }

object SettlementPlacementOverlayImpl extends VertexPlacementOverlayImpl[Settlement] {

  val settlementSize:Double = 5

  protected def doDraw( building:Building ):Boolean = building.isInstanceOf[Settlement]

  def points( hSize:Double, c:(Double, Double) ):List[(Double, Double)] = {
    val l = GameFieldPane.mult( settlementSize, hSize )
    List(
      (c._1 + l, c._2 + l),
      (c._1 - l, c._2 + l),
      (c._1 - l, c._2 - l),
      (c._1, c._2 - 2 * l),
      (c._1 + l, c._2 - l)
    )
  }
}
