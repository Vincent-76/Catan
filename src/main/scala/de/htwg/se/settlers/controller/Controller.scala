package de.htwg.se.settlers.controller

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.state.InitState
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util.Observable

import scala.util.{Failure, Success}

/**
 * @author Vincent76;
 */

class Controller( test:Boolean = false, debug:Boolean = false ) extends Observable {
  var running:Boolean = true
  var game:Game = Game( InitState( this ), test )
  private var undoStack:List[Command] = Nil
  private var redoStack:List[Command] = Nil

  def onTurn:PlayerID = game.turn.playerID

  def player:Player = player()

  def player( pID:PlayerID = onTurn ):Player = game.players( pID )

  def hasUndo:Boolean = undoStack.nonEmpty

  def hasRedo:Boolean = redoStack.nonEmpty

  private def checkWinner( newGame:Game ):Option[PlayerID] =
    newGame.players.values.find( p => p.getVictoryPoints( newGame ) >= Game.requiredVictoryPoints ) match {
      case Some( p ) => Some( p.id )
      case None => Option.empty
    }

  private def actionDone(newGame:Game, command:Command, newRedoStack:List[Command], info:Option[Info] ):Unit = {
    if( debug )
      println( "Done   | " + command )
    game = newGame
    undoStack = command :: undoStack
    redoStack = newRedoStack
    checkWinner( game ) match {
      case None =>
        update( info )
        //if ( info.isDefined ) info( info.get )
      case Some( pID ) =>
        game = game.copy( winner = Some( pID ) )
        exit()
        //info( GameEndInfo( pID ) )
    }
  }

  def action( command:Command ):Unit = {
    command.doStep( this, this.game ) match {
      case Success( (game, info) ) => actionDone( game, command, Nil, info )
      case Failure( t ) =>
        if( debug )
          println( "Error  | " + command )
        error( t )
    }
  }

  def undoAction( ):Unit = undoStack match {
    case Nil => error( NothingToUndo )
    case head :: stack =>
      this.game = head.undoStep( this.game )
      if( debug )
        println( "Undone | " + head )
      undoStack = stack
      redoStack = head :: redoStack
      update()
  }

  def redoAction( ):Unit = redoStack match {
    case Nil => error( NothingToRedo )
    case head :: stack =>
      val (game, info) = head.doStep( this, this.game ).get
      actionDone( game, head, stack, info )
  }

  def exit( ):Unit = {
    running = false
    update()
  }
}
