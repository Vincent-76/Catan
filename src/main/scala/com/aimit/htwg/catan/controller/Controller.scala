package com.aimit.htwg.catan.controller

import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model.FileIO.impls
import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.model.impl.fileio.{ JsonSerializable, XMLSerializable }
import com.aimit.htwg.catan.util.{ Observable, UndoManager }
import play.api.libs.json.{ JsSuccess, JsValue, Reads, Writes }

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */

object Controller {
  def apply( game:Game, fileIO:FileIO, undoStack:List[Command] = Nil, redoStack:List[Command] = Nil ):Controller =
    new Controller( game, fileIO, new UndoManager( undoStack, redoStack ) )
}

class Controller( var game:Game,
                  val fileIO:FileIO,
                  val undoManager:UndoManager = new UndoManager(),
                  var running:Boolean = true
                ) extends Observable {

  def onTurn:PlayerID = game.onTurn
  def player:Player = game.player( onTurn )
  def player( pID:PlayerID ):Player = game.player( pID )

  def hasUndo:Boolean = undoManager.hasUndo
  def hasRedo:Boolean = undoManager.hasRedo

  private def checkWinner( newGame:Game ):Option[PlayerID] =
    newGame.players.values.find( p => newGame.getPlayerVictoryPoints( p.id ) >= newGame.requiredVictoryPoints ) match {
      case Some( p ) => Some( p.id )
      case None => Option.empty
    }

  private def actionDone( newGame:Game, info:Option[Info] ):Try[Option[Info]] = {
    game = newGame
    checkWinner( game ) match {
      case None =>
        Success( update( info ) )
      case Some( pID ) =>
        game = game.setWinner( pID )
        exit( Some( GameEndInfo( pID ) ) )
        Success( info )
    }
  }

  def action( stateCommand:State => Option[Command] ):Try[Option[Info]] =
    action( stateCommand( game.state ) )

  def action( command:Option[Command] ):Try[Option[Info]] = {
    if( command.isEmpty ) {
      Failure( error( WrongState ) )
    } else undoManager.doStep( command.get, game ) match {
      case Success( (game, info) ) => actionDone( game, info )
      case Failure( t ) => Failure( error( t ) )
    }
  }

  def undoAction():Try[Option[Info]] = undoManager.undoStep( game ) match {
    case Success( newGame ) => actionDone( newGame, None )
    case Failure( t ) => Failure( error( t ) )
  }

  def redoAction():Try[Option[Info]] = undoManager.redoStep( game ) match {
    case Success( (newGame, info) ) => actionDone( newGame, info )
    case Failure( t ) => Failure( error( t ) )
  }

  def saveGame( fileName:Option[String] = None ):Try[Option[Info]] = {
    val path = if( fileName.isDefined )
      fileIO.save( game, undoManager.undoStack, undoManager.redoStack, fileName.get )
    else
      fileIO.save( game, undoManager.undoStack, undoManager.redoStack )
    Success( update( Some( GameSavedInfo( path ) ) ) )
  }

  def loadGame( path:String, extension:Option[String] = None ):Try[Option[Info]] = {
    val (newGame, undoStack, redoStack) = FileIO.load( path, extension )._2
    game = newGame
    undoManager.clear()
    undoManager.undoStack = undoStack
    undoManager.redoStack = redoStack
    Success( update( Some( GameLoadedInfo( path ) ) ) )
  }

  def exit( info:Option[Info] = None ):Try[Option[Info]] = {
    running = false
    Success( update( info ) )
  }
}
