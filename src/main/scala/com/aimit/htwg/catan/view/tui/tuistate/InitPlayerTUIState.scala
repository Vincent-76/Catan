package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.{ Info, InvalidPlayerColor, PlayerColor }
import com.aimit.htwg.catan.view.tui.{ CommandInput, TUI, TUIState }

import scala.util.{ Failure, Try }

/**
 * @author Vincent76;
 */
case class InitPlayerTUIState( controller:Controller ) extends TUIState {

  override def createStateDisplay:Iterable[String] = {
    List( "Players:" ) ++
    controller.game.players.values.map( p => TUI.displayName( p ) ) ++
    List( "Available colors: " +
      PlayerColor.availableColors( controller.game.players.values.map( _.color ) ).map( c => TUI.colorOf( c ) + c.title ).mkString( TUI.reset + ", " ),
      ""
    )
  }

  override def getActionInfo:String = "Type [<name> <color>] to add a player, or [next] to continue"

  override def inputPattern:Option[String] = Some( "(" + TUI.regexIgnoreCase( "next" ) + "|([a-zA-Z0-9]+\\s+(" +
    PlayerColor.availableColors( controller.game.players.values.map( _.color ) ).map( k => TUI.regexIgnoreCase( k.title ) ).mkString( "|" ) + ")))" )

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) = {
    if( commandInput.input.matches( TUI.regexIgnoreCase( "next" ) ) )
      (controller.action( _.setInitBeginnerState() ), Nil)
    else PlayerColor.of( commandInput.args.head ) match {
      case None => (Failure( controller.error( InvalidPlayerColor( commandInput.args.head ) ) ), Nil)
      case Some( playerColor ) =>
        (controller.action( _.addPlayer( playerColor, commandInput.command.get ) ), Nil)
    }
  }
}
