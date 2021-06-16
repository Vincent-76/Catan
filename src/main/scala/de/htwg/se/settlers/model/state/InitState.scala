package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.model.{ Command, State }
import de.htwg.se.settlers.model.commands.{ ChangeStateCommand, InitGameCommand }

/**
 * @author Vincent76;
 */
case class InitState( ) extends State {

  override def initGame( ):Option[Command] = Some(
    InitGameCommand()
  )

  //override def toString:String = getClass.getSimpleName
}
