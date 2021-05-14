package de.htwg.se.settlers.aview.gui

import scalafx.Includes._
import de.htwg.se.settlers.model.{ Game, KnightCard, Player }
import de.htwg.se.settlers.model.Cards._
import de.htwg.se.settlers.aview.gui.GUIApp._
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.effect.{ DropShadow, Glow }
import scalafx.scene.layout.{ AnchorPane, ColumnConstraints, GridPane, HBox, Priority, RowConstraints, StackPane, VBox }
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
class PlayerInfoPane( game:Game, p:Player ) extends AnchorPane {
  margin = Insets( 0, 10, 10, 10 )
  style = "-fx-border-color: " + GUIApp.colorOf( p.color ).toHex + "; -fx-background-color: #FFFFFF"
  effect = new DropShadow {
    offsetX = 0
    offsetY = 0
    color = GUIApp.colorOf( p.color )
    width = 5
    height = 5
  }
  val main:VBox = getInfoBox
  val bonusCards:VBox = new VBox {
    children = game.bonusCards.filter( d => d._2.isDefined && d._2.get._1 == p.id ).map( d => new StackPane {
      style = "-fx-border-color: #000000; -fx-border-width: 0 0 1 1; -fx-background-color: #FFFFFF"
      minWidth = 20
      minHeight = 20
      children = new Text( d._1.title.replaceAll( "[a-z\\s]", "" )
      )
    } ).toList
  }
  AnchorPane.setRightAnchor( bonusCards, 0 )
  AnchorPane.setTopAnchor( bonusCards, 0 )
  AnchorPane.setAnchors( main, 0, 0, 0, 0 )
  children = List( main, bonusCards )


  private def getInfoBox:VBox = new VBox {
    vgrow = Priority.Always
    padding = Insets( 5 )
    children = List(
      new HBox {
        children = List(
          new StackPane {
            minWidth = 25
            children = List(
              new Circle {
                radius = 9
                stroke = Color.Black
                strokeWidth = 1
                fill = Color.Transparent
              },
              new Text( game.getPlayerDisplayVictoryPoints( p.id ).toString )
            )
          },
          new Label( p.name ) {
            style = "-fx-font-size: 14"
            effect = new Glow( 0.7 )
            textFill = GUIApp.colorOf( p.color )
          }
        )
      },
      new GridPane {
        vgrow = Priority.Always
        columnConstraints = List( new ColumnConstraints {
          percentWidth = 100
        } )
        rowConstraints = ( 1 to 2 ).map( _ => new RowConstraints {
          percentHeight = 100 / 2
        } )
        add( new Text( "Resources  " + p.resources.amount ), 0, 0 )
        add( new HBox {
          hgrow = Priority.Always
          vgrow = Priority.Always
          spacing = 5
          children = List(
            new Text( "DevCards  " + p.devCards.size ),
            new Text( "Knights  " + p.usedDevCards.count( _ == KnightCard ) )
          )
        }, 0, 1 )
      }
    )
  }
}