package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ Command, State }
import de.htwg.se.catan.model.commands.PlaceRobberCommand

/**
 * @author Vincent76;
 */
case class RobberPlaceState( nextState:State ) extends State {

  override def placeRobber( hID:Int ):Option[Command] = Some(
    PlaceRobberCommand( hID, this )
  )

  //override def toString:String = getClass.getSimpleName + ": nextState[" + nextState + "]"
}
