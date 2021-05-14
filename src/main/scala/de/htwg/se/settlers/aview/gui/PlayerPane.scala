package de.htwg.se.settlers.aview.gui

import de.htwg.se.settlers.model.{ Cards, Game, Player }
import de.htwg.se.settlers.model.Cards._
import de.htwg.se.settlers.aview.gui.util.{ CustomDialog, FlowGridPane, GlowButton }
import de.htwg.se.settlers.util._
import scalafx.geometry.{ Insets, Orientation }
import scalafx.scene.effect.Glow
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
class PlayerPane( gui:GUI ) extends VBox {
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
                stroke = Color.Black
                strokeWidth = 1
                fill = Color.Transparent
              },
              new Text( game.getPlayerVictoryPoints( player.id ).toString ) {
                style = "-fx-font-size: 14"
              }
            )
          },
          new Text( player.name ) {
            style = "-fx-font-size: 18"
            effect = new Glow( 0.7 )
            fill = GUIApp.colorOf( player.color )
          }
        )
      },
    ) ++ ( if ( full )
      List(
        new FlowGridPane( 2 ) {
          padding = Insets( 10, 10, 10, 10 )
          hgrow = Priority.Always
          vgap = 10
          hgap = 10
          addAll( player.resources.sort.zipWithIndex.map( d => new BorderPane {
            hgrow = Priority.Always
            left = new Text( d._1._1.title ) {
              style = "-fx-font-size: 13"
              fill = GUIApp.colorOf( d._1._1 ).darker.darker
            }
            right = new Text( d._1._2.toString ) {
              style = "-fx-font-size: 13"
            }
          } ) )
        },
        new StackPane {
          margin = Insets( 0, 6, 0, 6 )
          style = "-fx-font-size: 14; -fx-border-color: #353535; -fx-border-width: 0 0 1 0"
          children = new Text( "Development Cards" )
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
