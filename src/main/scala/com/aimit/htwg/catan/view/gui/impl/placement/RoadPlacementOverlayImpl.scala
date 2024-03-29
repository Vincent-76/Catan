package com.aimit.htwg.catan.view.gui.impl.placement

import com.aimit.htwg.catan.Catan
import com.aimit.htwg.catan.view.gui.GUIApp
import com.aimit.htwg.catan.view.gui.gamefield.GameFieldPane.Coords
import com.aimit.htwg.catan.view.gui.gamefield.{ GameFieldPane, PlacementOverlay }
import com.aimit.htwg.catan.model.Game
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

object RoadPlacementOverlayImpl extends PlacementOverlay {

  val roadLength:Double = 0.5

  def draw( game:Game, context:GraphicsContext, coords:Coords, hSize:Double ):Unit = {
    game.gameField.edgeList.foreach( e => {
      if( Catan.debug || e.road.isDefined ) {
        game.gameField.adjacentVertices( e ) match {
          case List( vertex1, vertex2 ) =>
            val r = getRoadCoordinates(
              GUIApp.middleOf( coords( vertex1.h1 ), coords( vertex1.h2 ), coords( vertex1.h3 ) ),
              GUIApp.middleOf( coords( vertex2.h1 ), coords( vertex2.h2 ), coords( vertex2.h3 ) )
            )
            if( e.road.isDefined ) {
              context.stroke = Color.Black
              context.lineWidth = GameFieldPane.mult( 7, hSize )
              context.strokeLine( r._1._1, r._1._2, r._2._1, r._2._2 )
              context.stroke = GUIApp.colorOf( game.player( e.road.get.owner ).color )
              context.lineWidth = GameFieldPane.mult( 5, hSize )
              context.strokeLine( r._1._1, r._1._2, r._2._1, r._2._2 )
            }
            if( Catan.debug ) {
              context.fill = Color.White
              context.font = Font.font( Font.default.getFamily, GameFieldPane.mult( 10, hSize ) )
              val c = GUIApp.middleOf( r._1, r._2 )
              context.fillText( e.id.toString, c._1, c._2 )
            }
          case _ =>
        }
      }
    } )
  }

  private def getRoadCoordinates( v1:(Double, Double), v2:(Double, Double) ):((Double, Double), (Double, Double)) = {
    val a = (v2._1 - v1._1, v2._2 - v1._2)
    val mult = (1 - roadLength) / 2
    val p1 = (v1._1 + mult * a._1, v1._2 + mult * a._2)
    val p2 = (v1._1 + (1 - mult) * a._1, v1._2 + (1 - mult) * a._2)
    (p1, p2)
  }

}
