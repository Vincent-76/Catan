package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.GatherPhase
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }

/**
 * @author Vincent76;
 */
case class GatherAction( gatherPhase:GatherPhase, controller:Controller ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = Some( GameDisplay( controller ).buildGameField )

  override def actionInfo:Option[String] = {
    TUI.outln( gatherPhase.dices._1 + " + " + gatherPhase.dices._2 + " = " + ( gatherPhase.dices._1 + gatherPhase.dices._2 ) )
    gatherPhase.playerResources.foreach( data => {
      val p = controller.player( data._1 )
      TUI.outln( TUI.displayName( p ) + " " + data._2.filter( _._2 > 0 ).map( r => r._1.s + " +" + r._2 ).mkString( ", " ) )
    } )
    Some( "Press Enter to proceed" )
  }

  override def action( commandInput:CommandInput ):Option[Throwable] = controller.setActionPhase()

}
