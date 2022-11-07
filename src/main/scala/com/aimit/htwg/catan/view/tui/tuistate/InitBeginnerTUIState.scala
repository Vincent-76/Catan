package com.aimit.htwg.catan.view.tui.tuistate

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.view.tui.{ CommandInput, TUI, TUIState }
import com.aimit.htwg.catan.model.{ Info, PlayerID }

import scala.util.Try

/**
 * @author Vincent76;
 */
case class InitBeginnerTUIState( beginner:Option[PlayerID], diceValues:Map[PlayerID, Int], controller:Controller ) extends TUIState {

  override def createStateDisplay:Iterable[String] = {
    if( diceValues.isEmpty )
      List( "Time to dice who begins!" )
    else {
      val nameLength = controller.game.players.map( _._2.idName.length ).max
      val list = diceValues.filter( d => d._2 > 0 ).map( d => TUI.displayName( controller.game.players( d._1 ), nameLength ) + "   " + d._2 )
      TUI.outln()
      if( beginner.isDefined )
        list ++ List( "" ) ++ List( "\n->\t" + TUI.displayName( controller.player( beginner.get ) ) + " begins.\n" )
      else
        list ++ List( "" ) ++ List( "Tie!" )
    }
  }

  override def getActionInfo:String = {
    if( diceValues.isEmpty )
      "Press Enter to roll the dices"
    else if( beginner.isDefined )
      "Press Enter to proceed"
    else
      "Press Enter to roll again"
  }

  override def action( commandInput:CommandInput ):(Try[Option[Info]], List[String]) = {
    if( beginner.isDefined )
      (controller.action( _.setBeginner() ), Nil)
    else
      (controller.action( _.diceOutBeginner() ), Nil)
  }
}
