package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.view.tui.{ CommandInput, GameFieldDisplay, TUI, TUIState }
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.model.{ Info, PlayerID, Resource }
import com.aimit.htwg.catan.util._

import scala.util.Try

/**
 * @author Vincent76;
 */
case class DropHandCardsTUIState( pID:PlayerID, controller:Controller ) extends TUIState {

  override def createGameDisplay:Option[String] = Some(
    GameFieldDisplay.get( controller.game ).buildGameField
  )

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player( pID ) ) + ", you have to drop " +
      (controller.game.player( pID ).resources.amount / 2) + " cards!" )
    TUI.outln( "Resources:" )
    val resourceNameLength = Resource.impls.map( _.title.length ).max
    TUI.outln( controller.game.player( pID ).resources.sort.map( d =>
      "  " + d._1.title.toLength( resourceNameLength ) + " " + d._2 ).mkString( "\n" )
    )
    "Type [<Resource> <amount>, ...] to drop"
  }

  override def inputPattern:Option[String] = Some( "((^|,)" + TUI.resourcePattern + ")+" )

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) =
    (controller.dropResourceCardsToRobber( TUI.parseResources( commandInput.input ) ), Nil)
}
