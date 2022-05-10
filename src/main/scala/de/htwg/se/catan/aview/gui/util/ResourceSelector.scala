package de.htwg.se.catan.aview.gui.util

import de.htwg.se.catan.aview.gui.{ GUI, GUIApp }
import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.Resource
import scalafx.geometry.{ Orientation, Pos }
import scalafx.scene.image.ImageView
import scalafx.scene.layout.*
import scalafx.scene.text.Text

import scala.util.Try

/**
 * @author Vincent76;
 */
class ResourceSelector( maximum:ResourceCards = Map.empty,
                        initial:ResourceCards = Map.empty,
                        maxAmount:Option[Int] = Option.empty ) extends TilePane:

  val counter:List[(Resource, ResourceCounter)] = Resource.impls.toList.sortBy( _.index ).map( r => (r, new ResourceCounter( r, initial.getOrElse( r, 0 ) )) )

  def values:ResourceCards = counter.map( d => (d._1, d._2.value) ).toMap

  alignment = Pos.Center
  orientation = Orientation.Horizontal
  hgap = 8
  vgap = 8
  children = counter.map( d => {
    d._2.setValue( d._2.value )
    new VBox {
      minWidth = 50
      alignment = Pos.Center
      children = List(
        new Text( if( maximum.contains( d._1 ) ) "Max: " + maximum( d._1 ) else "" ) {
          style = "-fx-font-size: 14"
          fill = GUI.colorOf( d._1 ).darker.darker
        },
        new ImageView( GUI.resourceIcons( d._1 ) ) {
          fitWidth = 40
          preserveRatio = true
        },
        new BorderPane {
          hgrow = Priority.Always
          alignment = Pos.Center
          spacing = 3
          left = new GlowButton( "-" ) {
            minWidth = 16
            onMouseClicked = _ => d._2.decrement()
          }
          center = d._2
          right = new GlowButton( "+" ) {
            minWidth = 16
            onMouseClicked = _ => d._2.increment()
          }
        }
      )
    }
  } )

  class ResourceCounter( r:Resource, i:Int = 0 ) extends Text( i.toString ):
    style = "-fx-font-size: 14"

    def value:Int = Try( text.value.toInt ).getOrElse( 0 )

    def increment( ):Unit = setValue( value + 1 )

    def decrement( ):Unit = setValue( value - 1 )

    def setValue( v:Int ):Unit =
      if v <= 0 then
        set( 0 )
      else if( maximum.contains( r ) && v > maximum( r ) )
        set( maximum( r ) )
      else
        val amount = counter.filter( _._1 != r ).map( _._2.value ).sum
        if maxAmount.isDefined && v + amount > maxAmount.get then
          set( maxAmount.get - amount )
        else
          set( v )

    private def set( v:Int ):Unit = text = v.toString
