package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Player.PlayerColor
import de.htwg.se.settlers.model.{ Game, NotEnoughPlayers, State }
import de.htwg.se.settlers.model.commands.{ AddPlayerCommand, ChangeStateCommand }

/**
 * @author Vincent76;
 */
case class InitPlayerState( controller:Controller ) extends State( controller ) {

  override def addPlayer( playerColor:PlayerColor, name:String ):Unit = controller.action(
    AddPlayerCommand( playerColor, name, this )
  )

  override def setInitBeginnerState( ):Unit = if ( controller.game.players.size >= Game.minPlayers )
    controller.action( ChangeStateCommand( this, InitBeginnerState( controller ) ) )
  else controller.error( NotEnoughPlayers )
}
