package de.htwg.se.catan.util

import de.htwg.se.catan.model.{ Command, Game, Info, NothingToRedo, NothingToUndo }

import scala.util.{ Failure, Success, Try }

case class UndoManager(
                        var undoStack:List[Command] = Nil,
                        var redoStack:List[Command] = Nil
                      ) {

  def hasUndo:Boolean = undoStack.nonEmpty
  def hasRedo:Boolean = redoStack.nonEmpty

  def stepDone( command:Command, newRedoStack:List[Command], result:(Game, Option[Info]) ):Try[(Game, Option[Info])] = {
    undoStack = command :: undoStack
    redoStack = newRedoStack
    Success( result )
  }

  def doStep( command:Command, game:Game ):Try[(Game, Option[Info])] = {
    command.doStep( game ) match {
      case Success( result ) => stepDone( command, Nil, result )
      case f => f.rethrow
    }
  }

  def undoStep( game:Game ):Try[Game] = undoStack match {
    case Nil => Failure( NothingToUndo )
    case head :: stack =>
      val newGame = head.undoStep( game )
      undoStack = stack
      redoStack = head :: redoStack
      Success( newGame )
  }

  def redoStep( game:Game ):Try[(Game, Option[Info])] = redoStack match {
    case Nil => Failure( NothingToRedo )
    case head :: stack => head.doStep( game )  match {
      case Success( result ) => stepDone( head, stack, result )
      case f => f.rethrow
    }
  }

  def clear():Unit = {
    undoStack = Nil
    redoStack = Nil
  }
}
