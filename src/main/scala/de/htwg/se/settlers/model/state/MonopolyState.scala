package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.model.{ Command, Resource, State }
import de.htwg.se.settlers.model.commands.MonopolyCommand

/**
 * @author Vincent76;
 */
case class MonopolyState( nextState:State ) extends State {

  override def monopolyAction( r:Resource ):Option[Command] = Some(
    MonopolyCommand( r, this )
  )

  //override def toString:String = getClass.getSimpleName + ": nextState[" + nextState + "]"
}
