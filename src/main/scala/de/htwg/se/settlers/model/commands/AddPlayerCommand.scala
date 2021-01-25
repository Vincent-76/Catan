package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player.PlayerColor
import de.htwg.se.settlers.model.state.{ InitBeginnerState, InitPlayerState }
import de.htwg.se.settlers.model.{ Command, Game, Info, PlayerColorIsAlreadyInUse, PlayerNameAlreadyExists, PlayerNameEmpty, PlayerNameTooLong }
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class AddPlayerCommand( playerColor:PlayerColor, name:String, state:InitPlayerState ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    if( name.isEmpty )
      Failure( PlayerNameEmpty )
    else if( name.length > Game.maxPlayerNameLength )
      Failure( PlayerNameTooLong( name ) )
    else if ( game.players.exists( _._2.name =^ name ) )
      Failure( PlayerNameAlreadyExists( name ) )
    else if ( game.players.exists( _._2.color == playerColor ) )
      Failure( PlayerColorIsAlreadyInUse( playerColor ) )
    else {
      val newGame = game.addPlayer( playerColor, name )
      if ( newGame.players.size >= Game.maxPlayers )
        Success( newGame.setState( InitBeginnerState( controller ) ), Option.empty )
      else
        Success( newGame, Option.empty )
    }
  }

  override def undoStep( game:Game ):Game = game.copy(
    state = state,
    players = game.players.init
  )
}
