package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ InvalidPlayerColor, Player }
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI, TUIState }

/**
 * @author Vincent76;
 */
case class InitPlayerTUIState( controller:Controller ) extends TUIState {

  override def getActionInfo:String = {
    TUI.outln( "Players:" )
    controller.game.players.values.foreach( p => TUI.outln( TUI.displayName( p ) ) )
    TUI.outln( "Available colors: " +
      Player.availableColors( controller.game.players.values ).map( c => TUI.colorOf( c ) + c.name ).mkString( TUI.reset + ", " ) )
    "Type [<name> <color>] to add a player, or [next] to continue"
  }

  override def inputPattern:Option[String] = Some( "(" + TUI.regexIgnoreCase( "next" ) + "|([a-zA-Z0-9]+\\s+(" +
    Player.availableColors( controller.game.players.values ).map( k => TUI.regexIgnoreCase( k.name ) ).mkString( "|" ) + ")))" )

  override def action( commandInput:CommandInput ):Unit = {
    if ( commandInput.input.matches( TUI.regexIgnoreCase( "next" ) ) )
      controller.game.state.setInitBeginnerState()
    else Player.colorOf( commandInput.args.head ) match {
      case None => controller.error( InvalidPlayerColor( commandInput.args.head ) )
      case Some( playerColor ) =>
        controller.game.state.addPlayer( playerColor, commandInput.command.get )
    }
  }
}
