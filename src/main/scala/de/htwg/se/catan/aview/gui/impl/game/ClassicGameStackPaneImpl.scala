package de.htwg.se.catan.aview.gui.impl.game

import de.htwg.se.catan.aview.gui.{ GUI, GUIApp, GameStackPane }
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import de.htwg.se.catan.model.*
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{ ColumnConstraints, GridPane, Pane, VBox }
import scalafx.scene.paint.Color
import scalafx.scene.text.{ Text, TextAlignment }

class ClassicGameStackPaneImpl extends GameStackPane[ClassicGameImpl]:

  val resourceStacks:Map[Resource, Text] = Resource.impls.map( r => {
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
      styleClass.add( "gameStackHeader" )
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
      new ImageView( if r.isDefined then GUI.resourceIcons( r.get ) else GUI.devCardIcon ) {
        fitWidth = 40
        preserveRatio = true
      },
      if r.isDefined then resourceStacks( r.get ) else devStack
    )
  }

  protected def doUpdate( game:ClassicGameImpl ):Unit = {
    resourceStacks.foreach( d => d._2.text = game.resourceStack( d._1 ).toString )
    devStack.text = game.developmentCards.size.toString
  }
