package de.htwg.se.catan.aview.gui.impl.player

import de.htwg.se.catan.aview.gui.util.{ CustomDialog, FlowGridPane, GlowButton }
import de.htwg.se.catan.aview.gui.{ GUI, GUIApp, PlayerDisplay }
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import de.htwg.se.catan.model.Card._
import de.htwg.se.catan.model.{ Card, DevelopmentCard, Game }
import de.htwg.se.catan.util._
import scalafx.geometry.{ Insets, Orientation, Pos }
import scalafx.scene.Node
import scalafx.scene.control.Label
import scalafx.scene.image.ImageView
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text

case class ClassicPlayerDisplayImpl( player:ClassicPlayerImpl ) extends PlayerDisplay:

  def build( gui:GUI, game:Game, full:Boolean ):List[Node] = List(
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
              stroke = GUI.colorOf( player.color )
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
          textFill = GUI.colorOf( player.color )
        }
      )
    },
  ) ++ (if full then
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
            new ImageView( GUI.resourceIcons( d._1._1 ) ) {
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
        children = player.devCards.sortBy( _.title ).map( devCard => new GlowButton( devCard.title ) {
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
  else Nil)
