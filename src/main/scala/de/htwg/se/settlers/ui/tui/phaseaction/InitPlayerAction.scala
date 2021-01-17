package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Player, PlayerColor }
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI }

/**
 * @author Vincent76;
 */
case class InitPlayerAction( controller:Controller ) extends PhaseAction( controller ) {

  val availableColors:Vector[PlayerColor] = Player.availableColors( controller.game.players )

  override def actionInfo:Option[String] = {
    TUI.outln( "Players:" )
    controller.game.players.foreach( p => TUI.outln( TUI.displayName( p ) ) )
    TUI.outln( "Available colors: " + availableColors.map( c => c.c.t + c.name ).mkString( TUI.reset + ", " ) )
    Some( "Type [<name> <color>] to add a player, or [next] to continue" )
  }

  override def inputPattern:Option[String] = Some( "(" + TUI.regexIgnoreCase( "next" ) + "|([a-zA-Z0-9]+\\s+(" +
    availableColors.map( k => TUI.regexIgnoreCase( k.name ) ).mkString( "|" ) + ")))" )

  override def action( commandInput:CommandInput ):Option[Throwable] = {
    if ( commandInput.input.matches( TUI.regexIgnoreCase( "next" ) ) )
      controller.setInitBeginnerPhase()
    else
      controller.addPlayer( commandInput.args( 0 ), commandInput.command.get )
  }
}
