package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.RobberStealPhase
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success }

/**
 * @author Vincent76;
 */
case class RobberStealAction( robberStealPhase:RobberStealPhase, controller:Controller ) extends PhaseAction( controller ) {

  val adjacentPlayers:List[Int] = controller.game.adjacentPlayers( controller.gameField.robber )

  override def getGameDisplay:Option[String] = Some( GameDisplay( controller ).buildGameField )

  override def actionInfo:Option[String] = {
    TUI.outln( "Players to steal from:" )
    adjacentPlayers.foreach( pID => {
      val p = controller.player( pID )
      TUI.outln( TUI.displayName( p ) + " with " + p.resources.amount + "Cards" )
    } )
    Some( "Type [<PlayerID>] to select a player to steal from" )
  }

  override def inputPattern:Option[String] = Some( "(" + adjacentPlayers.mkString( "|" ) + ")" )

  override def action( commandInput:CommandInput ):Option[Throwable] = {
    controller.robberStealFromPlayer( commandInput.input.toInt ) match {
      case Success( resource ) =>
        if ( resource.isDefined )
          TUI.outln( "You stole 1 " + resource.get.s + "." )
        else
          TUI.outln( "You stole nothing." )
        TUI.awaitKey()
        Option.empty
      case Failure( e ) => Some( e )
    }
  }

}
