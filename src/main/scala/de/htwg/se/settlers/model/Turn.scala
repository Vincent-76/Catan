package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.cards.DevelopmentCard

/**
 * @author Vincent76;
 */
final case class Turn( playerID:PlayerID,
                       usedDevCard:Boolean = false,
                       drawnDevCards:List[DevelopmentCard] = List.empty ) {

  def addDrawnDevCard( card:DevelopmentCard ):Turn = copy( drawnDevCards = drawnDevCards :+ card )

  def removeDrawnDevCard():Turn = copy( drawnDevCards = drawnDevCards.init )

  def getLastDrawnDevCard:Option[DevelopmentCard] = drawnDevCards.lastOption
}
