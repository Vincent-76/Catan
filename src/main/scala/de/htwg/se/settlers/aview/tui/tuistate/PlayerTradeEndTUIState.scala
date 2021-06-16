package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.aview.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.{ InvalidPlayerID, PlayerID }

/**
 * @author Vincent76;
 */
case class PlayerTradeEndTUIState( give:ResourceCards, get:ResourceCards, decisions:Map[PlayerID, Boolean], controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game, Some( controller.game.onTurn ) )
  )

  override def getActionInfo:String = {
    TUI.outln( "Give: " + give.toString )
    TUI.outln( "Get: " + get.toString )
    if( decisions.exists( _._2 ) ) {
      val nameLength = decisions.map( d => controller.game.player( d._1 ).idName.length ).max
      decisions.foreach( data => {
        TUI.outln( TUI.displayName( controller.game.player( data._1 ), nameLength ) +
          "   " + (if( data._2 ) "Yes" else "No") )
      } )
      "Type [<PlayerID>] to accept the trade, or [X] to abort"
    } else {
      TUI.outln( "No trade partner found!" )
      "Press Enter to abort"
    }
  }

  override def inputPattern:Option[String] = if( decisions.exists( _._2 ) )
    Some( "[1-9][0-9]?" )
  else Option.empty

  override def action( commandInput:CommandInput ):Unit = if( decisions.exists( _._2 ) ) {
    controller.game.getPlayerID( commandInput.input.toInt ) match {
      case Some( pID ) => controller.playerTrade( pID )
      case None => controller.error( InvalidPlayerID( commandInput.input.toInt ) )
    }
  } else controller.abortPlayerTrade()

}
