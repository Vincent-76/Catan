package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.model.Game
import de.htwg.se.settlers.model.cards.Cards._
import de.htwg.se.settlers.aview.gui.util.{CustomDialog, FlowGridPane, GlowButton}
import de.htwg.se.settlers.model.cards.Cards
import de.htwg.se.settlers.model.player.Player
import de.htwg.se.settlers.util._
import scalafx.geometry.{Insets, Orientation, Pos}
import scalafx.scene.control.Label
import scalafx.scene.effect.Glow
import scalafx.scene.image.ImageView
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
class PlayerPane( gui:GUI ) extends VBox {
  background = GUIApp.woodBackground
  hgrow = Priority.Always
  style = "-fx-border-color: #353535; -fx-border-width: 0 0 2 0"
  minHeight = 220


  def update( game:Game, playerData:Option[(Player, Boolean)] ):Unit = children = playerData match {
    case None => Nil
    case Some( (player, full) ) => List(
      new HBox {
        padding = Insets( 8 )
        spacing = 6
        hgrow = Priority.Always
        style = "-fx-border-color: #353535; -fx-border-width: 0 0 1 0"
        children = List(
          new StackPane {
            minWidth = 30
            children = List(
              new Circle {
                radius = 12
                //stroke = Color.White
                strokeWidth = 2
                stroke = GUIApp.colorOf( player.color )
                fill = Color.Transparent
              },
              new Text( game.getPlayerVictoryPoints( player.id ).toString ) {
                fill = Color.White
                style = "-fx-font-size: 14; -fx-font-weight: bold;"
              }
            )
          },
          new Label( player.name ) {
            styleClass.add( "playerInfoName" )
            style = "-fx-font-size: 22;"
            textFill = GUIApp.colorOf( player.color )
          }
        )
      },
    ) ++ ( if ( full )
      List(
        new FlowGridPane( 3 ) {
          padding = Insets( 10, 10, 10, 10 )
          hgrow = Priority.Always
          vgap = 10
          hgap = 10
          addAll( player.resources.sort.zipWithIndex.map( d => new VBox {
            hgrow = Priority.Always
            alignment = Pos.Center
            children = List(
              new ImageView( GUIApp.resourceIcons( d._1._1 ) ) {
                fitWidth = 40
                preserveRatio = true
              },
              new Text( d._1._2.toString ) {
                styleClass.add( "resourceCounter" )
                fill = Color.White
              }
            )
          } ) )
        },
        new StackPane {
          margin = Insets( 0, 6, 0, 6 )
          style = "-fx-font-size: 14; -fx-border-color: #FFFFFF; -fx-border-width: 0 0 1 0"
          children = new Text( "Development Cards" ) {
            fill = Color.White
            style = "-fx-font-size: 16"
          }
        },
        new TilePane {
          hgrow = Priority.Always
          hgap = 6
          vgap = 6
          padding = Insets( 6 )
          orientation = Orientation.Horizontal
          prefColumns = 2
          children = player.devCards.sortBySeq( Cards.devCards ).map( devCard => new GlowButton( devCard.title ) {
            minWidth = 60
            onMouseClicked = _ => new CustomDialog( gui, "Development Card" ) {
              headerText = devCard.title
              content = new Text( devCard.desc ) {
                wrappingWidth = 300
              }
            }.show()
          } )
        }
      )
    else Nil )
  }
}
