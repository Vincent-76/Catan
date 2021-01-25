package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Resource, State }
import de.htwg.se.settlers.model.commands.MonopolyCommand

/**
 * @author Vincent76;
 */
case class MonopolyState( controller:Controller,
                          nextState:State ) extends State( controller ) {

  override def monopolyAction( r:Resource ):Unit = controller.action( MonopolyCommand( r, this ) )

}
