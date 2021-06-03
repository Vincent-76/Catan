package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.model._
import javafx.geometry.Side
import javafx.scene.layout.{BackgroundImage, BackgroundPosition, BackgroundRepeat, BackgroundSize}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.AccessibleRole.ImageView
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.text.{Text, TextAlignment}
import scalafx.scene.paint.Color

/**
 * @author Vincent76;
 */
class GameStackPane extends VBox {
  val resourceStacks:Map[Resource, Text] = Resources.get.map( r => {
    (r, new Text( "0" ) {
      fill = Color.White
      styleClass.add( "resourceCounter" )
    })
  } ).toMap
  val devStack:Text = new Text( "0" ) {
    fill = Color.White
    styleClass.add( "resourceCounter" )
  }
  padding = Insets( 4, 4, 4, 4 )
  fillWidth = true
  style = "-fx-border-color: #353535; -fx-border-width: 0 0 2 0;"
  children = List(
    new Text( "Stacks" ) {
      fill = Color.White
      style = "-fx-font-size: 18; -fx-font-weight: bold; -fx-stroke: black; -fx-stroke-width: 0.5px"
      //styleClass = styleClass :+ ""
      textAlignment = TextAlignment.Center
      margin = Insets( 0, 8, 8, 8 )
    },
    new GridPane() {
      columnConstraints = List( new ColumnConstraints {
        percentWidth = 33
      }, new ColumnConstraints {
        percentWidth = 33
      }, new ColumnConstraints {
        percentWidth = 33
      } )
      hgap = 10
      vgap = 8
      add( createStack( Some( Wood ) ), 0, 0 )
      add( createStack( Some( Clay ) ), 1, 0 )
      add( createStack( Some( Sheep ) ), 2, 0 )
      add( createStack( Some( Wheat ) ), 0, 1 )
      add( createStack( Some( Ore ) ), 1, 1 )
      add( createStack( None ), 2, 1 )
    }
  )

  private def createStack( r:Option[Resource] ):Pane = new VBox() {
    alignment = Pos.Center
    children = List(
      new ImageView( if( r.isDefined ) GUIApp.resourceIcons( r.get ) else GUIApp.devCardIcon ) {
        fitWidth = 40
        preserveRatio = true
      },
      if( r.isDefined ) resourceStacks( r.get ) else devStack
    )
  }

  def update( game:Game ):Unit = {
    resourceStacks.foreach( d => d._2.text = game.resourceStack( d._1 ).toString )
    devStack.text = game.developmentCards.size.toString
  }
}
