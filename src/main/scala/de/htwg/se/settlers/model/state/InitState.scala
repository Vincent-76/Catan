package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.model.{ Command, State }
import de.htwg.se.settlers.model.commands.ChangeStateCommand

/**
 * @author Vincent76;
 */
case class InitState( ) extends State {

  override def initPlayers( ):Option[Command] = Some(
    ChangeStateCommand( this, InitPlayerState() )
  )

  //override def toString:String = getClass.getSimpleName
}
