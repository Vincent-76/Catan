package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Game, NotEnoughPlayers, PlayerColor, State }
import de.htwg.se.settlers.model.commands.{ AddPlayerCommand, ChangeStateCommand }

/**
 * @author Vincent76;
 */
abstract class InitPlayerState( controller:Controller ) extends State( controller ) {

  override def addPlayer( playerColor:PlayerColor, name:String ):Unit = controller.action(
    AddPlayerCommand( playerColor, name, this )
  )

  override def setInitBeginnerState( ):Unit = if ( controller.game.players.size >= Game.minPlayers )
    controller.action( ChangeStateCommand( this, controller.ui.getInitBeginnerState() ) )
  else onError( NotEnoughPlayers )
}
