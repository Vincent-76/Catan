package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.commands.{ RollDicesCommand, UseDevCardCommand }

/**
 * @author Vincent76;
 */
case class DiceState( controller:Controller ) extends State( controller ) {

  override def useDevCard( devCard:DevelopmentCard ):Unit = controller.action( UseDevCardCommand( devCard, this ) )


  override def rollTheDices( ):Unit = controller.action(
    RollDicesCommand( this )
  )

}
