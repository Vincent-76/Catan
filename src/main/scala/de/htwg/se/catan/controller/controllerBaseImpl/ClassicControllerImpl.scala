package de.htwg.se.catan.controller.controllerBaseImpl

import com.google.inject.Inject
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.*
import de.htwg.se.catan.model.error.WrongState
import de.htwg.se.catan.model.info.{ GameEndInfo, GameLoadedInfo, GameSavedInfo }
import de.htwg.se.catan.util.UndoManager

import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */

class ClassicControllerImpl @Inject() ( var gameVal:Game, val fileIO:FileIO ) extends Controller:
  var running:Boolean = true
  private val undoManager:UndoManager = UndoManager()
  /*private var undoStack:List[Command] = Nil
  private var redoStack:List[Command] = Nil*/

  def game:Game = gameVal

  def hasUndo:Boolean = undoManager.hasUndo
  def hasRedo:Boolean = undoManager.hasRedo

  private def checkWinner( newGame:Game ):Option[PlayerID] =
    newGame.players.values.find( p => newGame.getPlayerVictoryPoints( p.id ) >= newGame.requiredVictoryPoints ) match
      case Some( p ) => Some( p.id )
      case None => Option.empty

  private def actionDone( newGame:Game, /*command:Command, newRedoStack:List[Command],*/ info:Option[Info] ):ActionResult =
    gameVal = newGame
    /*undoStack = command :: undoStack
    redoStack = newRedoStack*/
    checkWinner( game ) match
      case None =>
        update( game, info )
        //if ( info.isDefined ) info( info.get )
      case Some( pID ) =>
        gameVal = game.setWinner( pID )
        exit( Some( GameEndInfo( pID ) ) )
      ActionResult( newGame, info )

  def action( command:Option[Command] ):Try[ActionResult] =
    if command.isEmpty then
      Failure( error( WrongState ) )
    else undoManager.doStep( command.get, game ) match
      case Success( (game, info) ) => Success( actionDone( game, info ) )
      case Failure( t ) => Failure( error( t ) )
    /*else command.get.doStep( this.game ) match {
      case Success( (game, info) ) => actionDone( game, command.get, Nil, info )
      case Failure( t ) =>
        error( t )
    }*/

  def undoAction():Try[ActionResult] = undoManager.undoStep( game ) match
    case Success( newGame ) => Success( actionDone( newGame, None ) )
    case Failure( t ) => Failure( error( t ) )
    /*undoStack match {
    case Nil => error( NothingToUndo )
    case head :: stack =>
      gameVal = head.undoStep( this.game )
      undoStack = stack
      redoStack = head :: redoStack
      update()
  }*/

  def redoAction():Try[ActionResult] = undoManager.redoStep( game ) match
    case Success( (newGame, info) ) => Success( actionDone( newGame, info ) )
    case Failure( t ) => Failure( error( t ) )
    /*redoStack match {
    case Nil => error( NothingToRedo )
    case head :: stack =>
      val (game, info) = head.doStep( this.game ).get
      actionDone( game, head, stack, info )
  }*/

  def saveGame( ):Future[String] =
    val future = fileIO.save( game, undoManager.undoStack, undoManager.redoStack )
    future.onComplete {
      case Success( path ) => update( game, info = Some( GameSavedInfo( path ) ) )
      case Failure( f ) => error( f )
    }
    future

  def loadGame( path:String ):Try[ActionResult] =
    try {
      val (newGame, undoStack, redoStack) = FileIO.load( path )
      gameVal = newGame
      undoManager.clear()
      undoManager.undoStack = undoStack
      undoManager.redoStack = redoStack
      val info:Option[Info] = Some( GameLoadedInfo( path ) )
      update( newGame, info = info )
      Success( ActionResult( newGame, info ) )
    } catch {
      case t:Throwable => Failure( t )
    }

  def exit( info:Option[Info] = None ):Unit =
    running = false
    update( gameVal, info )
