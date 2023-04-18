package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.util.{ ActionHeader, FlowGridPane }
import de.htwg.se.catan.aview.gui.{ GUI, GUIApp, GUICommand, GUIState }
import de.htwg.se.catan.model.Card._
import de.htwg.se.catan.model.Player
import de.htwg.se.catan.model.state.PlayerTradeState
import scalafx.geometry.{ Insets, Pos }
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{ BorderPane, HBox, Priority, VBox }
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class PlayerTradeGUIState( state:PlayerTradeState, gui:GUI ) extends GUIState:

  override def getActions:List[GUICommand] = List( ( gui:GUI ) => new BorderPane {
    vgrow = Priority.Always
    //val p:Player = controller.player( state.pID )
    top = new ActionHeader( "Do you want to trade with " + gui.game.player.name + "?" )
    center = new VBox {
      spacing = 10
      alignment = Pos.Center
      hgrow = Priority.Always
      children = List(
        new Text( "Give" ) {
          fill = Color.White
          style = "-fx-font-size: 16; -fx-font-weight: bold;"
        },
        resourceDisplay( state.get ),
        new Text( "Get" ) {
          fill = Color.White
          style = "-fx-font-size: 16; -fx-font-weight: bold;"
        },
        resourceDisplay( state.give )
      )
    }
    bottom = new HBox {
      spacing = 10
      alignmentInParent = Pos.Center
      children = List(
        new Button( "Yes" ) {
          onAction = _ => gui.api.playerTradeDecision( true )
        },
        new Button( "No" ) {
          onAction = _ => gui.api.playerTradeDecision( false )
        }
      )
    }
  } )

  private def resourceDisplay( resources:ResourceCards ):FlowGridPane = new FlowGridPane( 3 ) {
    padding = Insets( 10, 10, 10, 10 )
    hgrow = Priority.Always
    vgap = 10
    hgap = 10
    addAll( resources.sort.filter( _._2 > 0 ).map( d => new VBox {
      hgrow = Priority.Always
      alignment = Pos.Center
      children = List(
        new ImageView( GUI.resourceIcons( d._1 ) ) {
          fitWidth = 40
          preserveRatio = true
        },
        new Text( d._2.toString ) {
          styleClass.add( "resourceCounter" )
          fill = Color.White
        }
      )
    } ) )
  }

  override def playerDisplayed:Option[(Player, Boolean)] = Some( gui.game.player( state.pID ), true )