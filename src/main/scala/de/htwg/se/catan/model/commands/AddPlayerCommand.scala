package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.state.{ InitBeginnerState, InitPlayerState }
import de.htwg.se.catan.model._
import de.htwg.se.catan.util._

import scala.util.{ Failure, Try }

/**
 * @author Vincent76;
 */
case class AddPlayerCommand( playerColor:PlayerColor, name:String, state:InitPlayerState ) extends Command {

  override def doStep( game:Game ):Try[CommandSuccess] = {
    if( name.isEmpty )
      Failure( PlayerNameEmpty )
    else if( name.length > game.maxPlayerNameLength )
      Failure( PlayerNameTooLong( name ) )
    else if ( game.players.exists( _._2.name ^= name ) )
      Failure( PlayerNameAlreadyExists( name ) )
    else if ( game.players.exists( _._2.color == playerColor ) )
      Failure( PlayerColorIsAlreadyInUse( playerColor ) )
    else {
      val newGame = game.addPlayer( playerColor, name )
      if ( newGame.players.size >= game.maxPlayers )
        success( newGame.setState( InitBeginnerState() ) )
      else
        success( newGame )
    }
  }

  override def undoStep( game:Game ):Game = game.removeLastPlayer().setState( state )

  //override def toString:String = getClass.getSimpleName + ": Name[" + name + "], Color[" + playerColor.name + "]"
}
