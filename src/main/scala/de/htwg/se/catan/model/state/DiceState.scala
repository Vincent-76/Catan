package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.{ DevelopmentCard, _ }
import de.htwg.se.catan.model.commands.{ RollDicesCommand, UseDevCardCommand }

import scala.xml.Node

/**
 * @author Vincent76;
 */

object DiceState {
  def fromXML( node:Node ):DiceState = DiceState()
}

case class DiceState() extends State {

  def toXML:Node = <DiceState />

  override def useDevCard( devCard:DevelopmentCard ):Option[Command] = Some(
    UseDevCardCommand( devCard, this )
  )


  override def rollTheDices( ):Option[Command] = Some(
    RollDicesCommand( this )
  )

  //override def toString:String = getClass.getSimpleName
}
