package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, Resource, State }
import de.htwg.se.catan.model.commands.MonopolyCommand

/**
 * @author Vincent76;
 */
case class MonopolyState( nextState:State ) extends State {

  override def monopolyAction( r:Resource ):Option[Command] = Some(
    MonopolyCommand( r, this )
  )

  //override def toString:String = getClass.getSimpleName + ": nextState[" + nextState + "]"
}
