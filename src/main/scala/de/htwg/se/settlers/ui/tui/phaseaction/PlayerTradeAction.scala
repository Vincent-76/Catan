package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Player, PlayerTradePhase }
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
object PlayerTradeAction {
  def apply( tradePhase:PlayerTradePhase, controller:Controller ):PlayerTradeAction = {
    PlayerTradeAction( tradePhase, controller, controller.checkTradePlayerInOrder( tradePhase ) )
  }
}

case class PlayerTradeAction( tradePhase:PlayerTradePhase, controller:Controller, player:Option[Player] ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    val playerID = if ( player.isDefined ) player.get.id else controller.onTurn
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( playerID ) ) )
  }

  override def actionInfo:Option[String] = {
    if ( player.isDefined ) {
      TUI.outln( TUI.displayName( player.get ) + ", do you want to trade?" )
      TUI.outln( "Get:\t" + tradePhase.give.display )
      TUI.outln( "Give:\t" + tradePhase.get.display )
      controller.setPlayerTradeDecision( player.get, TUI.confirmed() )
      return Option.empty
    }
    if ( !tradePhase.decisions.exists( _._2 ) ) {
      TUI.outln( "No trade partner found!" )
      controller.abortPlayerTrade()
      TUI.awaitKey()
      return Option.empty
    }
    val decisions = tradePhase.decisions.map( d => (controller.player( d._1 ), d._2) )
    val nameLength = decisions.map( _._1.idName.length ).max
    decisions.foreach( data => {
      TUI.outln( TUI.displayName( data._1, nameLength ) + "   " + ( if ( data._2 ) "Yes" else "No" ) )
    } )
    Some( "Type [<PlayerID>] to accept the trade, or [X] to abort" )
  }

  override def inputPattern:Option[String] = Some( "(" + tradePhase.decisions.filter( _._2 ).keys.mkString( "|" ) +
    "|" + TUI.regexIgnoreCase( "x" ) + ")" )

  override def action( commandInput:CommandInput ):Option[Throwable] = {
    if ( commandInput.input.toLowerCase == "x" )
      controller.abortPlayerTrade()
    else
      controller.playerTrade( commandInput.input.toInt )
  }
}
