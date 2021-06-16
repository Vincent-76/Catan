package de.htwg.se.catan.aview.gui.impl.placement

import de.htwg.se.catan.Catan
import de.htwg.se.catan.aview.gui.GUIApp
import de.htwg.se.catan.aview.gui.gamefield.GameFieldPane.Coords
import de.htwg.se.catan.aview.gui.gamefield.{ GameFieldPane, PlacementOverlay }
import de.htwg.se.catan.model.{ Building, Game }
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

abstract class VertexPlacementOverlayImpl[T <: Building] extends PlacementOverlay {

  def draw( game:Game, context:GraphicsContext, coords:Coords, hSize:Double ):Unit = {
    context.stroke = Color.Black
    context.lineWidth = 1
    game.gameField.vertexList.foreach( v => {
      if( Catan.debug || v.building.isDefined ) {
        val c = GUIApp.middleOf( coords( v.h1 ), coords( v.h2 ), coords( v.h3 ) )
        if( v.building.isDefined && doDraw( v.building.get ) ) {
          context.fill = GUIApp.colorOf( game.player( v.building.get.owner ).color )
          val p = points( hSize, c )
          context.fillPolygon( p )
          context.strokePolygon( p )
        }
        if( Catan.debug ) {
          context.fill = Color.White
          context.font = Font.font( Font.default.getFamily, GameFieldPane.mult( 10, hSize ) )
          context.fillText( v.id.toString, c._1, c._2 )
        }
      }
    } )
  }

  protected def doDraw( building:Building ):Boolean

  def points( hSize:Double, c:(Double, Double) ):List[(Double, Double)]
}
