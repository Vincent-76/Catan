package com.aimit.htwg.catan.controller.controllerBaseImpl

import com.google.inject.Inject
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.util.UndoManager

import scala.util.{ Failure, Success }

/**
 * @author Vincent76;
 */

class ClassicControllerImpl @Inject() ( var gameVal:Game, val fileIO:FileIO ) extends Controller {
  var running:Boolean = true
  private val undoManager:UndoManager = new UndoManager()
  /*private var undoStack:List[Command] = Nil
  private var redoStack:List[Command] = Nil*/

  def game:Game = gameVal

  def hasUndo:Boolean = undoManager.hasUndo
  def hasRedo:Boolean = undoManager.hasRedo

  private def checkWinner( newGame:Game ):Option[PlayerID] =
    newGame.players.values.find( p => newGame.getPlayerVictoryPoints( p.id ) >= newGame.requiredVictoryPoints ) match {
      case Some( p ) => Some( p.id )
      case None => Option.empty
    }

  private def actionDone( newGame:Game, /*command:Command, newRedoStack:List[Command],*/ info:Option[Info] ):Unit = {
    gameVal = newGame
    /*undoStack = command :: undoStack
    redoStack = newRedoStack*/
    checkWinner( game ) match {
      case None =>
        update( info )
        //if ( info.isDefined ) info( info.get )
      case Some( pID ) =>
        gameVal = game.setWinner( pID )
        exit( Some( GameEndInfo( pID ) ) )
    }
  }

  def action( command:Option[Command] ):Unit = {
    if( command.isEmpty )
      error( WrongState )
    else undoManager.doStep( command.get, game ) match {
      case Success( (game, info) ) => actionDone( game, info )
      case Failure( t ) => error( t )
    }
    /*else command.get.doStep( this.game ) match {
      case Success( (game, info) ) => actionDone( game, command.get, Nil, info )
      case Failure( t ) =>
        error( t )
    }*/
  }

  def undoAction():Unit = undoManager.undoStep( game ) match {
    case Success( newGame ) => actionDone( newGame, None )
    case Failure( t ) => error( t )
  }
    /*undoStack match {
    case Nil => error( NothingToUndo )
    case head :: stack =>
      gameVal = head.undoStep( this.game )
      undoStack = stack
      redoStack = head :: redoStack
      update()
  }*/

  def redoAction():Unit = undoManager.redoStep( game ) match {
    case Success( (newGame, info) ) => actionDone( newGame, info )
    case Failure( t ) => error( t )
  }
    /*redoStack match {
    case Nil => error( NothingToRedo )
    case head :: stack =>
      val (game, info) = head.doStep( this.game ).get
      actionDone( game, head, stack, info )
  }*/

  def saveGame():String = {
    val path = fileIO.save( game, undoManager.undoStack, undoManager.redoStack )
    update( info = Some( GameSavedInfo( path ) ) )
    path
  }

  def loadGame( path:String ):Unit = {
    val (newGame, undoStack, redoStack) = FileIO.load( path )
    gameVal = newGame
    undoManager.clear()
    undoManager.undoStack = undoStack
    undoManager.redoStack = redoStack
    update( info = Some( GameLoadedInfo( path ) ) )
  }

  def exit( info:Option[Info] = None ):Unit = {
    running = false
    update( info )
  }

}
