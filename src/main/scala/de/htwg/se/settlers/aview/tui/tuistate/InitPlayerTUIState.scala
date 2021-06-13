package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ InvalidPlayerColor, PlayerColor }
import de.htwg.se.settlers.aview.tui.{ CommandInput, TUI, TUIState }

/**
 * @author Vincent76;
 */
case class InitPlayerTUIState( controller:Controller ) extends TUIState {

  override def getActionInfo:String = {
    TUI.outln( "Players:" )
    controller.game.players.values.foreach( p => TUI.outln( TUI.displayName( p ) ) )
    TUI.outln( "Available colors: " +
      PlayerColor.availableColors( controller.game.players.values.map( _.color ) ).map( c => TUI.colorOf( c ) + c.name ).mkString( TUI.reset + ", " ) )
    "Type [<name> <color>] to add a player, or [next] to continue"
  }

  override def inputPattern:Option[String] = Some( "(" + TUI.regexIgnoreCase( "next" ) + "|([a-zA-Z0-9]+\\s+(" +
    PlayerColor.availableColors( controller.game.players.values.map( _.color ) ).map( k => TUI.regexIgnoreCase( k.name ) ).mkString( "|" ) + ")))" )

  override def action( commandInput:CommandInput ):Unit = {
    if( commandInput.input.matches( TUI.regexIgnoreCase( "next" ) ) )
      controller.setInitBeginnerState()
    else PlayerColor.colorOf( commandInput.args.head ) match {
      case None => controller.error( InvalidPlayerColor( commandInput.args.head ) )
      case Some( playerColor ) =>
        controller.addPlayer( playerColor, commandInput.command.get )
    }
  }
}
