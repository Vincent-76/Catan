package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.view.tui.{ CommandInput, TUI, TUIState }
import com.aimit.htwg.catan.model.{ Info, PlayerID }

import scala.util.Try

/**
 * @author Vincent76;
 */
case class InitBeginnerTUIState( beginner:Option[PlayerID], diceValues:Map[PlayerID, Int], controller:Controller ) extends TUIState {

  override def getActionInfo:String = {
    if( diceValues.isEmpty ) {
      TUI.outln( "Time to dice who begins!" )
      "Press Enter to roll the dices"
    } else {
      val nameLength = controller.game.players.map( _._2.idName.length ).max
      diceValues.foreach( d => if( d._2 > 0 ) TUI.outln( TUI.displayName( controller.game.players( d._1 ), nameLength ) + "   " + d._2 ) )
      TUI.outln()
      if( beginner.isDefined ) {
        TUI.outln( "\n->\t" + TUI.displayName( controller.player( beginner.get ) ) + " begins.\n" )
        "Press Enter to proceed"
      } else {
        TUI.outln( "Tie!" )
        "Press Enter to roll again"
      }
    }
  }

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) = {
    if( beginner.isDefined )
      (controller.setBeginner(), Nil)
    else
      (controller.diceOutBeginner(), Nil)
  }
}
