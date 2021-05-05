package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.model._
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.layout._
import scalafx.scene.text.{ Text, TextAlignment }

/**
 * @author Vincent76;
 */
class GameStackPane extends VBox {
  val resourceStacks:Map[Resource, Text] = Resources.get.map( r => {
    (r, new Text( "0" ))
  } ).toMap
  val devStack:Text = new Text( "0" )
  padding = Insets( 10, 6, 14, 6 )
  fillWidth = true
  style = "-fx-border-color: #353535; -fx-border-width: 0 0 2 0;"
  children = List(
    new Text( "Stacks" ) {
      textAlignment = TextAlignment.Center
      margin = Insets( 0, 8, 8, 8 )
    },
    new GridPane() {
      columnConstraints = List( new ColumnConstraints {
        percentWidth = 50
      }, new ColumnConstraints {
        percentWidth = 50
      } )
      hgap = 10
      vgap = 8
      add( createStack( Wood.title, resourceStacks( Wood ) ), 0, 0 )
      add( createStack( Clay.title, resourceStacks( Clay ) ), 1, 0 )
      add( createStack( Sheep.title, resourceStacks( Sheep ) ), 0, 1 )
      add( createStack( Wheat.title, resourceStacks( Wheat ) ), 1, 1 )
      add( createStack( Ore.title, resourceStacks( Ore ) ), 0, 2 )
      add( createStack( "Dev", devStack ), 1, 2 )
    }
  )

  private def createStack( title:String, amount:Text ):Pane = new Pane {
    alignment = Pos.Center
    children = List(
      new BorderPane {
        minWidth = 60
        //padding = Insets( 6, 0, 6, 0 )
        left = new Text( title )
        right = amount
      }
    )
  }

  def update( game:Game ):Unit = {
    resourceStacks.foreach( d => d._2.text = game.resourceStack( d._1 ).toString )
    devStack.text = game.developmentCards.size.toString
  }
}
