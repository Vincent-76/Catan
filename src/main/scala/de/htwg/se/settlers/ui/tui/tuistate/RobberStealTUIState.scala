package de.htwg.se.settlers.ui.tui.tuistate

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ InvalidPlayerID, State }
import de.htwg.se.settlers.model.state.RobberStealState
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI, TUIState }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
class RobberStealTUIState( nextState:State,
                           controller:Controller
                         ) extends RobberStealState( nextState, controller ) with TUIState {

  override def getGameDisplay:Option[String] = Some( GameDisplay( controller ).buildGameField )

  override def getActionInfo:String = {
    TUI.outln( "Players to steal from:" )
    controller.game.gameField.adjacentPlayers( controller.game.gameField.robber ).foreach( pID => {
      val p = controller.game.player( pID )
      TUI.outln( TUI.displayName( p ) + " with " + p.resources.amount + "Cards" )
    } )
    "Type [<PlayerID>] to select a player to steal from"
  }

  override def inputPattern:Option[String] = Some( "[1-9][0-9]?" )

  override def action( commandInput:CommandInput ):Unit = controller.game.getPlayerID( commandInput.input.toInt ) match {
    case Some( pID ) => robberStealFromPlayer( pID )
    case None => onError( InvalidPlayerID( commandInput.input.toInt )  )
  }
}
