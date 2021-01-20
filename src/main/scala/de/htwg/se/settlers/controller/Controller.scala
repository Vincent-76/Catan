package de.htwg.se.settlers.controller

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.{ Command, Game, GameEndInfo, Info, NothingToRedo, NothingToUndo, Player }
import de.htwg.se.settlers.ui.UI

import scala.util.{ Failure, Success }

/**
 * @author Vincent76;
 */

class Controller( uiString:String, test:Boolean = false ) {
  var running:Boolean = true
  val ui:UI = UI.get( uiString, this )
  var game:Game = Game( ui.getInitState, test )
  private var undoStack:List[Command] = Nil
  private var redoStack:List[Command] = Nil

  ui.start()

  def onTurn:PlayerID = game.turn.playerID

  def player:Player = player()

  def player( pID:PlayerID = onTurn ):Player = game.players( pID )

  private def checkWinner( newGame:Game ):Option[PlayerID] =
    newGame.players.values.find( p => p.getVictoryPoints( newGame ) >= Game.requiredVictoryPoints ) match {
      case Some( p ) => Some( p.id )
      case None => Option.empty
    }

  private def actionDone( newGame:Game, command:Command, newRedoStack:List[Command], info:Option[Info] ):Unit = {
    game = newGame
    undoStack = command :: undoStack
    redoStack = newRedoStack
    checkWinner( game ) match {
      case None =>
        //if ( info.isEmpty || !ui.onInfoWait( info.get ) )
        if ( info.isDefined ) ui.onInfo( info.get )
        game.state.show()
      case Some( pID ) =>
        running = false
        game = game.copy( winner = Some( pID ) )
        ui.onInfo( GameEndInfo( pID ) )
    }
  }

  def action( command:Command ):Unit = {
    command.doStep( this, this.game ) match {
      case Success( (game, info) ) => actionDone( game, command, Nil, info )
      case Failure( t ) => this.game.state.onError( t )
    }
  }

  def undoAction( ):Unit = undoStack match {
    case Nil => ui.onError( NothingToUndo )
    case head :: stack =>
      this.game = head.undoStep( this.game )
      undoStack = stack
      redoStack = head :: redoStack
      this.game.state.show()
  }

  def redoAction( ):Unit = redoStack match {
    case Nil => ui.onError( NothingToRedo )
    case head :: stack =>
      val (game, info) = head.doStep( this, this.game ).get
      actionDone( game, head, stack, info )
  }

  def exit( ):Unit = running = false
}
