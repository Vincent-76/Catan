package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Resources, State }
import de.htwg.se.settlers.model.state.MonopolyState
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI, TUIState }

/**
 * @author Vincent76;
 */
class MonopolyTUIState( nextState:State,
                        controller:Controller
                      ) extends MonopolyState( nextState, controller ) with TUIState {

  override def getGameDisplay:Option[String] = {
    val gameDisplay = GameDisplay( controller )
    Some( gameDisplay.buildGameField + gameDisplay.buildPlayerDisplay( Some( controller.game.onTurn ) ) )
  }

  override def getActionInfo:String = {
    TUI.outln( "You can specify a resource to get all the corresponding cards from the other players" )
    "Type [<" + Resources.get.map( _.s ).mkString( "|" ) + ">] to specify a resource"
  }

  override def inputPattern:Option[String] =
    Some( "(" + Resources.get.map( r => TUI.regexIgnoreCase( r.s ) ).mkString( "|" ) + ")" )

  override def action( commandInput:CommandInput ):Unit = monopolyAction( Resources.of( commandInput.input ).get )
}
