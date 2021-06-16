package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, State }
import de.htwg.se.catan.model.commands.{ ChangeStateCommand, InitGameCommand }

/**
 * @author Vincent76;
 */
case class InitState( ) extends State {

  override def initGame( ):Option[Command] = Some(
    InitGameCommand()
  )

  //override def toString:String = getClass.getSimpleName
}
