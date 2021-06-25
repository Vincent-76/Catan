package de.htwg.se.catan.aview.tui.tuistate

import de.htwg.se.catan.aview.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.Resource

/**
 * @author Vincent76;
 */
case class MonopolyTUIState( controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game, Some( controller.game.onTurn ) )
  )

  override def getActionInfo:String = {
    TUI.outln( "You can specify a resource to get all the corresponding cards from the other players" )
    "Type [<" + Resource.impls.map( _.title ).mkString( "|" ) + ">] to specify a resource"
  }

  override def inputPattern:Option[String] =
    Some( "(" + Resource.impls.map( r => TUI.regexIgnoreCase( r.title ) ).mkString( "|" ) + ")" )

  override def action( commandInput:CommandInput ):Unit =
    controller.monopolyAction( Resource.of( commandInput.input ).get )
}
