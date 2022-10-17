package com.aimit.htwg.catan.view.gui

import com.aimit.htwg.catan.model.{Game, KnightCard, Player}
import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.view.gui.GUIApp._
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.{AnchorPane, ColumnConstraints, GridPane, HBox, Priority, RowConstraints, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
class PlayerInfoPane( game:Game, p:Player ) extends AnchorPane {
  background = GUIApp.woodBackground
  margin = Insets( 0, 10, 10, 10 )
  style = "-fx-border-color: #000000"
  /*effect = new DropShadow {
    offsetX = 0
    offsetY = 0
    color = GUIApp.colorOf( p.color )
    width = 5
    height = 5
  }*/
  val main:VBox = getInfoBox
  val bonusCards:VBox = new VBox {
    children = game.bonusCards.filter( d => d._2.isDefined && d._2.get._1 == p.id ).map( d => new StackPane {
      style = "-fx-border-color: " + GUIApp.colorOf( p.color ).toHex + "; -fx-border-width: 0 0 1 1; -fx-background-color: #5a5a5a"
      minWidth = 20
      minHeight = 20
      children = new Text( d._1.title.replaceAll( "[a-z\\s]", "" ) ) {
        fill = Color.White
      }
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
                //stroke = Color.White
                strokeWidth = 2
                stroke = GUIApp.colorOf( p.color )
                fill = Color.Transparent
              },
              new Text( game.getPlayerDisplayVictoryPoints( p.id ).toString ) {
                fill = Color.White
                style = "-fx-font-size: 12; -fx-font-weight: bold;"
              }
            )
          },
          new Label( p.name ) {
            textFill = GUIApp.colorOf( p.color )
            style = "-fx-font-size: 16;"
            styleClass.add( "playerInfoName" )
            //effect = new Glow( 0.7 )
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
        add( new Text( "Resources  " + p.resources.amount ) {
          styleClass.add( "playerInfoPaneDataLabel" )
        }, 0, 0 )
        add( new HBox {
          hgrow = Priority.Always
          vgrow = Priority.Always
          spacing = 5
          children = List(
            new Text( "DevCards  " + p.devCards.size ) {
              styleClass.add( "playerInfoPaneDataLabel" )
            },
            new Text( "Knights  " + p.usedDevCards.count( _ == KnightCard ) ) {
              styleClass.add( "playerInfoPaneDataLabel" )
            }
          )
        }, 0, 1 )
      }
    )
  }
}