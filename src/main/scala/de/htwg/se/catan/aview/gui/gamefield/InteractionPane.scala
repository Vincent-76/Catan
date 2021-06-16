package de.htwg.se.catan.aview.gui.gamefield

import de.htwg.se.catan.aview.gui.impl.placement.RobberPlacementOverlayImpl
import de.htwg.se.catan.aview.gui.{ FieldInputDisplayState, GUIApp }
import de.htwg.se.catan.model.{ Edge, Hex, Vertex }
import scalafx.Includes._
import scalafx.scene.layout.{ AnchorPane, StackPane }
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle

/**
 * @author Vincent76;
 */
object InteractionPane {
  val circleRadius:Double = 8
  val circleStroke:Double = 2
}

class InteractionPane extends AnchorPane {
  var input:Option[FieldInputDisplayState] = None

  def resetInput( ):Unit = this.input = None

  def setInput( input:FieldInputDisplayState ):Unit = this.input = Some( input )

  def update( coords:Map[Hex, (Double, Double)], hSize:Double ):Unit = children = input match {
    case Some( input ) =>
      val circleRadius = GameFieldPane.mult( InteractionPane.circleRadius, hSize )
      input.points.map {
        case h:Hex =>
          val p = coords( h )
          (h.id, (p._1, p._2 - RobberPlacementOverlayImpl.robberDist * hSize))
        case e:Edge =>
          (e.id, GUIApp.middleOf( coords( e.h1 ), coords( e.h2 ) ))
        case v:Vertex =>
          (v.id, GUIApp.middleOf( coords( v.h1 ), coords( v.h2 ), coords( v.h3 ) ))
      }.map( p => {
        new StackPane {
          children = List(
            new Circle {
              radius = circleRadius + 1
              stroke = Color.Gold
              strokeWidth = GameFieldPane.mult( InteractionPane.circleStroke, hSize )
              fill = Color.Transparent
            },
            new Circle {
              radius = circleRadius
              stroke = Color.Gold.darker
              strokeWidth = GameFieldPane.mult( InteractionPane.circleStroke, hSize )
              fill = Color.Transparent
              onMouseEntered = _ => fill = Color.Gold.brighter.opacity( 0.6 )
              onMouseExited = _ => fill = Color.Transparent
              onMouseClicked = _ => input.action( p._1 )
            }
          )
          AnchorPane.setLeftAnchor( this, p._2._1 - circleRadius - 2 )
          AnchorPane.setTopAnchor( this, p._2._2 - circleRadius - 2 )
        }
      } )
    case _ => Nil
  }
}
