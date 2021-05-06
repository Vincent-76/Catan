package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.{Command, State}
import de.htwg.se.settlers.model.commands.DropHandCardsCommand

/**
 * @author Vincent76;
 */
case class DropHandCardsState( pID:PlayerID, dropped:List[PlayerID] = List.empty ) extends State {

  override def dropResourceCardsToRobber( cards:ResourceCards ):Option[Command] = Some(
    DropHandCardsCommand( this, cards )
  )

  //override def toString:String = getClass.getSimpleName + ": pID[" + pID + "], dropped[" + dropped.mkString( ", " ) + "]"
}
