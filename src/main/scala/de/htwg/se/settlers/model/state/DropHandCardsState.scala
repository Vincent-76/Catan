package de.htwg.se.settlers.model.state

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.model.commands.DropHandCardsCommand

/**
 * @author Vincent76;
 */
case class DropHandCardsState( controller:Controller, pID:PlayerID, dropped:List[PlayerID] = List.empty ) extends State( controller ) {

  override def dropResourceCardsToRobber( cards:ResourceCards ):Unit = controller.action(
    DropHandCardsCommand( this, cards )
  )
}
