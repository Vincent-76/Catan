package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.Catan
import de.htwg.se.settlers.model.GameField.Hex
import de.htwg.se.settlers.model.{City, Game, Settlement}
import scalafx.geometry.VPos
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, TextAlignment}

/**
 * @author Vincent76;
 */
object OverlayPane {
  val roadLength:Double = 0.5
  val settlementSize:Double = 5
  val citySize:Double = 6
  val robberDist:Double = 0.5
  val robberSize:Double = 6
}

class OverlayPane extends Canvas {

  graphicsContext2D.textAlign = TextAlignment.Center
  graphicsContext2D.textBaseline = VPos.Center

  def update( game:Game, coords:Map[Hex, (Double, Double)], hSize:Double ):Unit = {
    graphicsContext2D.clearRect( 0, 0, width.value, height.value )
    updateEdges( game, coords, hSize )
    updateVertices( game, coords, hSize )
    val p = coords( game.gameField.robber )
    val points = getRobberCoordinates( hSize, (p._1, p._2 - OverlayPane.robberDist * hSize ) )
    graphicsContext2D.fill = Color.DarkSlateGray
    graphicsContext2D.fillPolygon( points )
    graphicsContext2D.stroke = Color.Black
    graphicsContext2D.lineWidth = 1
    graphicsContext2D.strokePolygon( points )
  }

  private def updateEdges( game:Game, coords:Map[Hex, (Double, Double)], hSize:Double ):Unit = {
    game.gameField.edges.values.foreach( e => {
      if( Catan.debug || e.road.isDefined ) {
        game.gameField.adjacentVertices( e ) match {
          case List( vertex1, vertex2 ) =>
            val r = getRoadCoordinates(
              GUIApp.middleOf( coords( vertex1.h1 ), coords( vertex1.h2 ), coords( vertex1.h3 ) ),
              GUIApp.middleOf( coords( vertex2.h1 ), coords( vertex2.h2 ), coords( vertex2.h3 ) )
            )
            if( e.road.isDefined ) {
              graphicsContext2D.stroke = Color.Black
              graphicsContext2D.lineWidth = GameFieldPane.mult( 7, hSize )
              graphicsContext2D.strokeLine( r._1._1, r._1._2, r._2._1, r._2._2 )
              graphicsContext2D.stroke = GUIApp.colorOf( game.player( e.road.get.owner ).color )
              graphicsContext2D.lineWidth = GameFieldPane.mult( 5, hSize )
              graphicsContext2D.strokeLine( r._1._1, r._1._2, r._2._1, r._2._2 )
            }
            if( Catan.debug ) {
              graphicsContext2D.fill = Color.White
              graphicsContext2D.font = Font.font( Font.default.getFamily, GameFieldPane.mult( 10, hSize ) )
              val c = GUIApp.middleOf( r._1, r._2 )
              graphicsContext2D.fillText( e.id.toString, c._1, c._2 )
            }
          case _ =>
        }
      }
    } )
  }

  private def getRoadCoordinates( v1:(Double, Double), v2:(Double, Double) ):((Double, Double), (Double, Double)) = {
    val a = (v2._1 - v1._1, v2._2 - v1._2)
    val mult = ( 1 - OverlayPane.roadLength ) / 2
    val p1 = (v1._1 + mult * a._1, v1._2 + mult * a._2)
    val p2 = (v1._1 + ( 1 - mult ) * a._1, v1._2 + ( 1 - mult ) * a._2)
    (p1, p2)
  }


  private def updateVertices( game:Game, coords:Map[Hex, (Double, Double)], hSize:Double ):Unit = {
    graphicsContext2D.stroke = Color.Black
    graphicsContext2D.lineWidth = 1
    game.gameField.vertices.values.foreach( v => {
      if( Catan.debug || v.building.isDefined ) {
        val p = GUIApp.middleOf( coords( v.h1 ), coords( v.h2 ), coords( v.h3 ) )
        if( v.building.isDefined ) {
          graphicsContext2D.fill = GUIApp.colorOf( game.player( v.building.get.owner ).color )
          val points = v.building.get match {
            case _:Settlement => getSettlementCoordinates( hSize, p )
            case _:City => getCityCoordinates( hSize, p )
          }
          graphicsContext2D.fillPolygon( points )
          graphicsContext2D.strokePolygon( points )
        }
        if( Catan.debug ) {
          graphicsContext2D.fill = Color.White
          graphicsContext2D.font = Font.font( Font.default.getFamily, GameFieldPane.mult( 10, hSize ) )
          graphicsContext2D.fillText( v.id.toString, p._1, p._2 )
        }
      }
    } )
  }

  private def getSettlementCoordinates( hSize:Double, p:(Double, Double) ):List[(Double, Double)] = {
    val l = GameFieldPane.mult( OverlayPane.settlementSize, hSize )
    List(
      (p._1 + l, p._2 + l),
      (p._1 - l, p._2 + l),
      (p._1 - l, p._2 - l),
      (p._1, p._2 - 2 * l),
      (p._1 + l, p._2 - l)
    )
  }

  private def getCityCoordinates( hSize:Double, p:(Double, Double) ):List[(Double, Double)] = {
    val l = GameFieldPane.mult( OverlayPane.citySize, hSize )
    List(
      (p._1 + 1.4 * l, p._2 + l),
      (p._1 - 1.4 * l, p._2 + l),
      (p._1 - 1.4 * l, p._2 - l),
      (p._1, p._2 - l),
      (p._1, p._2 - 2.5 * l),
      (p._1 + 0.7 * l, p._2 - 3 * l),
      (p._1 + 1.4 * l, p._2 - 2.5 * l)
    )
  }

  private def getRobberCoordinates( hSize:Double, p:(Double, Double) ):List[(Double, Double)] = {
    val l = GameFieldPane.mult( OverlayPane.robberSize, hSize )
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
