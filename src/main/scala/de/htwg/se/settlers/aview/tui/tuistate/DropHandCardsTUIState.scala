package de.htwg.se.settlers.aview.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.Resources
import de.htwg.se.settlers.aview.tui.{ CommandInput, GameDisplay, TUI, TUIState }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
case class DropHandCardsTUIState( pID:PlayerID, controller:Controller ) extends TUIState {

  override def getGameDisplay:Option[String] = Some( GameDisplay( controller ).buildGameField )

  override def getActionInfo:String = {
    TUI.outln( TUI.displayName( controller.game.player( pID ) ) + ", you have to drop " +
      ( controller.game.player( pID ).resources.amount / 2 ) + " cards!" )
    TUI.outln( "Resources:" )
    val resourceNameLength = Resources.get.map( _.title.length ).max
    TUI.outln( controller.game.player( pID ).resources.sort.map( d =>
      "  " + d._1.title.toLength( resourceNameLength ) + " " + d._2 ).mkString( "\n" )
    )
    "Type [<Resource> <amount>, ...] to drop"
  }

  override def inputPattern:Option[String] = Some( "((^|,)" + TUI.resourcePattern + ")+" )

  override def action( commandInput:CommandInput ):Unit =
    controller.dropResourceCardsToRobber( TUI.parseResources( commandInput.input ) )
}
