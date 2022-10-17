package com.aimit.htwg.catan.view.gui.gamefield

import com.aimit.htwg.catan.model._
import scalafx.geometry.VPos
import scalafx.scene.canvas.Canvas
import scalafx.scene.text.TextAlignment

/**
 * @author Vincent76;
 */

class OverlayPane( placements:List[PlacementOverlay] ) extends Canvas {

  graphicsContext2D.textAlign = TextAlignment.Center
  graphicsContext2D.textBaseline = VPos.Center

  def update( game:Game, coords:Map[Hex, (Double, Double)], hSize:Double ):Unit = {
    graphicsContext2D.clearRect( 0, 0, width.value, height.value )
    placements.foreach( _.draw( game, graphicsContext2D, coords, hSize ) )
  }

}
