package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model.{ Info, InvalidPlayerID, PlayerID }

import scala.util.{ Failure, Try }

/**
 * @author Vincent76;
 */
case class PlayerTradeEndTUIState( give:ResourceCards, get:ResourceCards, decisions:Map[PlayerID, Boolean], controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game, Some( controller.game.onTurn ) )
  )

  override def createStateDisplay:Iterable[String] = {
    val list = List(
      "Give: " + give.toString,
      "Get: " + get.toString
    )
    if( decisions.exists( _._2 ) ) {
      val nameLength = decisions.map( d => controller.game.player( d._1 ).idName.length ).max
      list ++ decisions.toList.map( data => TUI.displayName( controller.game.player( data._1 ), nameLength ) +
        "   " + (if( data._2 ) "Yes" else "No")
      )
    }
    else
      list ++ List( "No trade partner found!" )
  }

  override def getActionInfo:String = {
    if( decisions.exists( _._2 ) )
      "Type [<PlayerID>] to accept the trade, or [X] to abort"
    else
      "Press Enter to abort"
  }

  override def inputPattern:Option[String] = if( decisions.exists( _._2 ) )
    Some( "[1-9][0-9]?" )
  else Option.empty

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) = if( decisions.exists( _._2 ) ) {
    controller.game.getPlayerID( commandInput.input.toInt ) match {
      case Some( pID ) => (controller.action( _.playerTrade( pID ) ), Nil)
      case None => (Failure( controller.error( InvalidPlayerID( commandInput.input.toInt ) ) ), Nil)
    }
  } else (controller.action( _.abortPlayerTrade() ), Nil)

}
