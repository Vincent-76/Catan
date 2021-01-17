package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
case class InitBeginnerAction( controller:Controller ) extends PhaseAction( controller ) {

  override def actionInfo:Option[String] = {
    TUI.outln( "Time to dice who begins!" )
    Some( "Press Enter to roll the dices" )
  }

  override def action( commandInput:CommandInput ):Option[Throwable] = {
    val id = diceOutBeginner( controller.game.players.indices.map( _ => controller.rollDice() ) )
    TUI.outln( "\n->\t" + TUI.displayName( controller.player( id ) ) + " begins.\n" )
    TUI.awaitKey()
    controller.setInitBuildPhase( id )
  }

  private def diceOutBeginner( values:Seq[Int] ):Int = {
    TUI.outln( "Values: " )
    val nameLength = controller.game.players.map( _.idName.length ).max
    for ( (x, i) <- values.view.zipWithIndex )
      if ( x > 0 ) TUI.outln( TUI.displayName( controller.game.players( i ), nameLength ) + "   " + x )
    val maxValue = values.max
    val beginners = values.count( _ >= maxValue )
    if ( beginners > 1 ) {
      TUI.outln( "Tie!" )
      TUI.awaitKey( "Press enter to roll again" )
      return diceOutBeginner( values.map( value => if ( value < maxValue ) 0 else controller.rollDice() ) )
    }
    values.indexOf( maxValue )
  }
}
