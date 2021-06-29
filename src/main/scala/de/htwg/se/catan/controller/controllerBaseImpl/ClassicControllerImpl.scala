package de.htwg.se.catan.controller.controllerBaseImpl

import com.google.inject.Inject
import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model._
import de.htwg.se.catan.model.impl.game.ClassicGameImpl

import scala.util.{ Failure, Success }

/**
 * @author Vincent76;
 */

class ClassicControllerImpl @Inject() ( var gameVal:Game, val fileIO:FileIO ) extends Controller {
  var running:Boolean = true
  //var gameVal:Game = ClassicGameImpl( gameField = ClassicGameFieldImpl() )
  private var undoStack:List[Command] = Nil
  private var redoStack:List[Command] = Nil

  def game:Game = gameVal

  def hasUndo:Boolean = undoStack.nonEmpty
  def hasRedo:Boolean = redoStack.nonEmpty

  private def checkWinner( newGame:Game ):Option[PlayerID] =
    newGame.players.values.find( p => newGame.getPlayerVictoryPoints( p.id ) >= newGame.requiredVictoryPoints ) match {
      case Some( p ) => Some( p.id )
      case None => Option.empty
    }

  private def actionDone( newGame:Game, command:Command, newRedoStack:List[Command], info:Option[Info] ):Unit = {
    gameVal = newGame
    undoStack = command :: undoStack
    redoStack = newRedoStack
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
    else command.get.doStep( this.game ) match {
      case Success( (game, info) ) => actionDone( game, command.get, Nil, info )
      case Failure( t ) =>
        error( t )
    }
  }

  def undoAction():Unit = undoStack match {
    case Nil => error( NothingToUndo )
    case head :: stack =>
      gameVal = head.undoStep( this.game )
      undoStack = stack
      redoStack = head :: redoStack
      update()
  }

  def redoAction():Unit = redoStack match {
    case Nil => error( NothingToRedo )
    case head :: stack =>
      val (game, info) = head.doStep( this.game ).get
      actionDone( game, head, stack, info )
  }

  def saveGame():String = {
    val path = fileIO.save( game )
    update( info = Some( GameSavedInfo( path ) ) )
    path
  }

  def loadGame( path:String ):Unit = {
    gameVal = FileIO.load( path )
    undoStack = Nil
    redoStack = Nil
    update( info = Some( GameLoadedInfo( path ) ) )
  }

  def exit( info:Option[Info] = None ):Unit = {
    running = false
    update( info )
  }

}
