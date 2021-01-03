package de.htwg.se.settlers.controller

import de.htwg.se.settlers.model.{ Card, Game, Phase }
import de.htwg.se.settlers.ui.TUI

/**
 * @author Vincent76;
 */
class Controller {
  var running:Boolean = true
  var games:List[Game] = List( Game() )
  var undone:List[Game] = List()
  val ui:TUI = new TUI( this )

  def game:Game = games.head

  def undo( ):Boolean = {
    if ( games.size <= 1 )
      return false
    undone = games.head :: undone
    games = games.tail
    true
  }

  def redo( ):Boolean = {
    if( undone.isEmpty )
      return false
    games = undone.head :: games
    undone = undone.tail
    true
  }

  def exit( ):Unit = {
    running = false
  }

  private def action( newGame:Game ):Unit = {
    if ( games.size > 10 )
      games = newGame :: games.init
    else
      games = newGame :: games
    undone = List()
  }

  def addPlayer( name:String ):Unit = {
  }

  //def setTurn( turn:Int ):Unit = action( game.copy( turn = turn ) )

  def dropCard( card:Card ):Unit = dropCards( card :: Nil )

  def dropCards( cards:List[Card] ):Unit = action( game.dropCards( cards ) )

  def setPhase( phase:Phase ):Unit = action( game.setPhase( phase ) )
}
