package de.htwg.se.catan.model.state

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.model.{Command, PlayerID, State}
import de.htwg.se.catan.model.commands.DropHandCardsCommand

/**
 * @author Vincent76;
 */
case class DropHandCardsState( pID:PlayerID, dropped:List[PlayerID] = List.empty ) extends State {

  override def dropResourceCardsToRobber( cards:ResourceCards ):Option[Command] = Some(
    DropHandCardsCommand( this, cards )
  )

  //override def toString:String = getClass.getSimpleName + ": pID[" + pID + "], dropped[" + dropped.mkString( ", " ) + "]"
}
