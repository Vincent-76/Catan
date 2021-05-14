package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.Catan
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.aview.gui.GameFieldPane.Coords
import de.htwg.se.settlers.util._
import scalafx.geometry.VPos
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight, TextAlignment}

/**
 * @author Vincent76;
 */
object GameFieldCanvas {
  val portLength:Double = 0.3
  val hexBorderWidth:Double = 2
  val numberSize:Int = 10
  val numberMult:Double = 2
}

class GameFieldCanvas extends Canvas {

  graphicsContext2D.textAlign = TextAlignment.Center
  graphicsContext2D.textBaseline = VPos.Center

  def update(gameField:ClassicGameField, hWidth:Double, hSize:Double ):Coords = {
    graphicsContext2D.clearRect( 0, 0, width.value, height.value )
    val coords = gameField.hexagons.redByKey( Map.empty:Coords, ( coords:Coords, i:Int ) => {
      val row = gameField.hexagons( i )
      val fRow = row.filter( _.isDefined )
      val nulls = row.size - fRow.size
      fRow.redByKey( coords, ( coords:Coords, j:Int ) => {
        val hex = fRow( j )
        val x = GameFieldPane.padding + ( hWidth / 2 * ( nulls + 1 ) ) + j * hWidth
        val y = GameFieldPane.padding + hSize + i * ( 6d / 4d * hSize )
        coords.updated( hex.get, (x, y) )
      } )
    } )
    coords.foreach( d => drawWater( gameField, coords, hSize, d._1, d._2 ) )
    //coords.foreach( d => drawWater( gameField, coords, hSize, d._1, d._2 ) )
    coords.foreach( d => drawHex( hSize, d._1, d._2 ) )
    coords
  }

  private def drawHex( hSize:Double, h:Hex, center:(Double, Double) ):Unit = {
    val points = ( 0 to 5 ).map( i => hexCorner( hSize, center, i ) )
    h.area match {
      case a:LandArea =>
        graphicsContext2D.fill = GUIApp.colorOf( h.area.f )
        graphicsContext2D.fillPolygon( points )
        graphicsContext2D.lineWidth = GameFieldCanvas.hexBorderWidth
        graphicsContext2D.stroke = Color.Black.brighter
        graphicsContext2D.strokePolygon( points )
        a match {
          case ResourceArea( _, number ) =>
            graphicsContext2D.fill = Color.Black
            val fontSize = GameFieldCanvas.numberSize + number.frequency * GameFieldCanvas.numberMult
            graphicsContext2D.fill = if ( number.frequency >= 5 ) Color.Brown else Color.Black
            graphicsContext2D.font = Font.font( Font.default.getFamily, FontWeight.Bold, GameFieldPane.mult( fontSize, hSize ) )
            graphicsContext2D.fillText( number.value.toString, center._1, center._2 )
          case _ =>
        }
      case _ =>
    }
    if( Catan.debug ) {
      graphicsContext2D.fill = Color.White
      graphicsContext2D.font = Font.font( Font.default.getFamily, GameFieldPane.mult( 12, hSize ) )
      graphicsContext2D.fillText( h.id.toString, center._1, ( center._2 - hSize / 2 ) )
    }
  }

  private def drawWater(gameField:ClassicGameField, coords:Coords, hSize:Double, h:Hex, center:(Double, Double) ):Unit = h.area match {
    case w:WaterArea =>
      if ( w.port.isDefined ) {
        graphicsContext2D.font = Font.font( Font.default.getFamily, FontWeight.Bold, GameFieldPane.mult( 14, hSize ) )
        if ( w.port.get.specific.isDefined ) {
          graphicsContext2D.fill = GUIApp.colorOf( w.port.get.specific.get )
          graphicsContext2D.fillText( w.port.get.specific.get.title, center._1, center._2 )
        } else {
          graphicsContext2D.fill = Color.Black
          graphicsContext2D.fillText( "?", center._1, center._2 )
        }
        graphicsContext2D.stroke = Color.SaddleBrown.darker.darker
        graphicsContext2D.lineWidth = GameFieldPane.mult( 4, hSize )
        gameField.adjacentVertices( h ).filter( _.port == w.port ).foreach( v => {
          val m = GUIApp.middleOf( coords( v.h1 ), coords( v.h2 ), coords( v.h3 ) )
          val a = (center._1 - m._1, center._2 - m._2)
          graphicsContext2D.strokeLine( m._1, m._2,
            m._1 + GameFieldCanvas.portLength * a._1,
            m._2 + GameFieldCanvas.portLength * a._2 )
        } )
      }
      graphicsContext2D.stroke = GUIApp.colorOf( Water ).darker.darker.darker
      graphicsContext2D.lineWidth = GameFieldCanvas.hexBorderWidth
      graphicsContext2D.strokePolygon( ( 0 to 5 ).map( i => hexCorner( hSize, center, i ) ) )
    case _ =>
  }

  private def hexCorner( hSize:Double, center:(Double, Double), i:Int ):(Double, Double) = {
    val rad = Math.PI / 180 * ( 60 * i - 30 )
    (center._1 + hSize * Math.cos( rad ), center._2 + hSize * Math.sin( rad ))
  }
}
