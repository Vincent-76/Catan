package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.{ Info, Resource }

import scala.util.Try

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

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) =
    (controller.monopolyAction( Resource.of( commandInput.input ).get ), Nil)
}
