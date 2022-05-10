package de.htwg.se.catan.aview.gui.guistate

import de.htwg.se.catan.aview.gui.util.ActionHeader
import de.htwg.se.catan.aview.gui.{ GUI, GUIApp, GUICommand, GUIState }
import de.htwg.se.catan.model.Player
import de.htwg.se.catan.model.state.PlayerTradeEndState
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.{ BorderPane, Priority, VBox }
import scalafx.scene.text.Text

/**
 * @author Vincent76;
 */
case class PlayerTradeEndGUIState( state:PlayerTradeEndState, gui:GUI ) extends GUIState:

  override def getActions:List[GUICommand] = List( _ => new BorderPane {
    vgrow = Priority.Always
    top = new ActionHeader( "Choose trade player" )
    center = new VBox {
      spacing = 8
      alignmentInParent = Pos.Center
      alignment = Pos.Center
      children = state.decisions.filter( _._2 ).map( d => new VBox {
        spacing = 4
        alignment = Pos.Center
        children = List(
          new Text( gui.game.player( d._1 ).name ) {
            fill = GUI.colorOf( gui.game.player( d._1 ).color )
            styleClass.add( "playerInfoName" )
            style = "-fx-font-size: 16;"
          },
          new Button( "Trade" ) {
            styleClass.add( "button" )
            onAction = _ => gui.api.playerTrade( d._1 )
          }
        )
      } )
    }
    bottom = new Button( "Abort" ) {
      alignmentInParent = Pos.Center
      onAction = _ => gui.api.abortPlayerTrade()
    }
  } )

  override def playerDisplayed:Option[(Player, Boolean)] = Some( gui.game.player, true )