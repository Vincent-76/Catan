package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.model.{Command, State}
import de.htwg.se.catan.model.commands.YearOfPlentyCommand

/**
 * @author Vincent76;
 */
case class YearOfPlentyState( nextState:State ) extends State {

  override def yearOfPlentyAction( resources:ResourceCards ):Option[Command] = Some(
    YearOfPlentyCommand( resources, this )
  )

  //override def toString:String = getClass.getSimpleName + ": nextState[" + nextState + "]"
}
