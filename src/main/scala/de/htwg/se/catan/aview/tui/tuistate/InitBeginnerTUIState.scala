package de.htwg.se.catan.aview.tui.tuistate

import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.aview.tui.{ CommandInput, TUI, TUIState }
import de.htwg.se.catan.model.PlayerID

/**
 * @author Vincent76;
 */
case class InitBeginnerTUIState( beginner:Option[PlayerID], diceValues:Map[PlayerID, Int], controller:Controller ) extends TUIState:

  override def getActionInfo:String =
    if diceValues.isEmpty then
      TUI.outln( "Time to dice who begins!" )
      "Press Enter to roll the dices"
    else
      val nameLength = controller.game.players.map( _._2.idName.length ).max
      diceValues.foreach( d => if d._2 > 0 then TUI.outln( TUI.displayName( controller.game.players( d._1 ), nameLength ) + "   " + d._2 ) )
      TUI.outln()
      if beginner.isDefined then
        TUI.outln( "\n->\t" + TUI.displayName( controller.player( beginner.get ) ) + " begins.\n" )
        "Press Enter to proceed"
      else
        TUI.outln( "Tie!" )
        "Press Enter to roll again"

  override def action( commandInput:CommandInput ):Unit =
    if beginner.isDefined then
      controller.setBeginner()
    else
      controller.diceOutBeginner()
