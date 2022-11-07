package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.model.{ Info, InvalidPlayerID }
import com.aimit.htwg.catan.model.state.RobberStealState

import scala.util.{ Failure, Try }

/**
 * @author Vincent76;
 */
case class RobberStealTUIState( state:RobberStealState, controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField + buildPlayerDisplay( controller.game, Some( controller.game.onTurn ) )
  )

  override def createStateDisplay:Iterable[String] = {
    List( "Players to steal from:" ) ++
    state.adjacentPlayers.map( pID => {
      val p = controller.game.player( pID )
      TUI.displayName( p ) + " with " + p.resources.amount + "Cards"
    } )
  }

  override def getActionInfo:String = "Type [<PlayerID>] to select a player to steal from"

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) = controller.game.getPlayerID( commandInput.input.toInt ) match {
    case Some( pID ) => (controller.action( _.robberStealFromPlayer( pID ) ), Nil)
    case None => (Failure( controller.error( InvalidPlayerID( commandInput.input.toInt ) ) ), Nil)
  }
}
