package de.htwg.se.settlers.controller.controllerBaseImpl

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.impl.game.ClassicGameImpl
import de.htwg.se.settlers.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.settlers.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }

import scala.util.{ Failure, Success }

/**
 * @author Vincent76;
 */

class ClassicControllerImpl( test:Boolean = false /*, debug:Boolean = false*/ ) extends Controller {
  var running:Boolean = true
  var gameVal:Game = ClassicGameImpl( test = test, gameField = ClassicGameFieldImpl() )
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

  def exit( info:Option[Info] = None ):Unit = {
    running = false
    update( info )
  }

}
