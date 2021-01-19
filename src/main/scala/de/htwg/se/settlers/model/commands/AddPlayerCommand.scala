package de.htwg.se.settlers.model.commands

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.state.InitPlayerState
import de.htwg.se.settlers.model.{ Command, Game, Info, PlayerColor, PlayerColorIsAlreadyInUse, PlayerNameAlreadyExists }
import de.htwg.se.settlers.util._

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */
case class AddPlayerCommand( playerColor:PlayerColor, name:String, state:InitPlayerState ) extends Command {

  override def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])] = {
    if ( game.players.exists( _._2.name =^ name ) )
      Failure( PlayerNameAlreadyExists( name ) )
    else if ( game.players.exists( _._2.color == playerColor ) )
      Failure( PlayerColorIsAlreadyInUse( playerColor ) )
    else {
      val newGame = game.addPlayer( playerColor, name )
      if ( newGame.players.size >= Game.maxPlayers )
        Success( newGame.setState( controller.ui.getInitBeginnerState() ), Option.empty )
      else
        Success( newGame, Option.empty )
    }
  }

  override def undoStep( game:Game ):Game = game.copy(
    state = state,
    players = game.players.init
  )
}
