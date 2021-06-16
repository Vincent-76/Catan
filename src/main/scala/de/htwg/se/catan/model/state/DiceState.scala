package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{DevelopmentCard, _}
import de.htwg.se.catan.model.commands.{RollDicesCommand, UseDevCardCommand}

/**
 * @author Vincent76;
 */
case class DiceState() extends State {

  override def useDevCard( devCard:DevelopmentCard ):Option[Command] = Some(
    UseDevCardCommand( devCard, this )
  )


  override def rollTheDices( ):Option[Command] = Some(
    RollDicesCommand( this )
  )

  //override def toString:String = getClass.getSimpleName
}
