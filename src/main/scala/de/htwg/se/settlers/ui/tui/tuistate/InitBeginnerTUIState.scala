package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.state.InitBeginnerState
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI, TUIState }

/**
 * @author Vincent76;
 */
class InitBeginnerTUIState( diceValues:Map[PlayerID, Int],
                            counter:Int,
                            controller:Controller
                          ) extends InitBeginnerState( diceValues, counter, controller ) with TUIState {

  override def getActionInfo:String = {
    if ( diceValues.isEmpty ) {
      TUI.outln( "Time to dice who begins!" )
      "Press Enter to roll the dices"
    } else {
      val nameLength = controller.game.players.map( _._2.idName.length ).max
      diceValues.foreach( d => if ( d._2 > 0 ) TUI.outln( TUI.displayName( controller.game.players( d._1 ), nameLength ) + "   " + d._2 ) )
      TUI.outln()
      TUI.outln( "Tie!" )
      "Press Enter to roll again"
    }
  }

  override def action( commandInput:CommandInput ):Unit = diceOutBeginner()
}
