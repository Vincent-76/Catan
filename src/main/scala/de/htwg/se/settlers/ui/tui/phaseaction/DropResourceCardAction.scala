package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Player, Resources, WrongResourceAmount }
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
case class DropResourceCardAction( controller:Controller ) extends PhaseAction( controller ) {

  val player:Option[Player] = controller.checkHandCardsInOrder()

  override def getGameDisplay:Option[String] = Some( GameDisplay( controller ).buildGameField )

  override def actionInfo:Option[String] = {
    if ( player.isEmpty )
      return Option.empty
    TUI.outln( TUI.displayName( player.get ) + ", you have to drop " + ( player.get.resources.amount / 2 ) + " cards!" )
    TUI.outln( "Resources:" )
    val resourceNameLength = Resources.get.map( _.s.length ).max
    TUI.outln( player.get.resources.sort.map( d => "  " + d._1.s.toLength( resourceNameLength ) + " " + d._2 ).mkString( "\n" ) )
    Some( "Type [<Resource> <amount>, ...] to drop" )
  }

  override def inputPattern:Option[String] = Some( "((^|,)" + TUI.resourcePattern + ")+" )

  override def action( commandInput:CommandInput ):Option[Throwable] =
    controller.dropResourceCardsToRobber( player.get.id, TUI.parseResources( commandInput.input ) )
}
