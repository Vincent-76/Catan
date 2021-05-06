package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.model.Player.PlayerColor
import de.htwg.se.settlers.model.{Command, Game, NotEnoughPlayers, State}
import de.htwg.se.settlers.model.commands.{AddPlayerCommand, ChangeStateCommand, SetInitBeginnerStateCommand}

/**
 * @author Vincent76;
 */
case class InitPlayerState() extends State {

  override def addPlayer( playerColor:PlayerColor, name:String ):Option[Command] = Some(
    AddPlayerCommand( playerColor, name, this )
  )

  override def setInitBeginnerState( ):Option[Command] = Some(
    SetInitBeginnerStateCommand( this )
  )

  //override def toString:String = getClass.getSimpleName
}
