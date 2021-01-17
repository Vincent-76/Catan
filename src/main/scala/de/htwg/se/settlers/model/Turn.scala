package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */
final case class Turn( playerID:Int,
                       usedDevCard:Boolean = false,
                       drawnDevCards:List[DevelopmentCard] = List.empty ) {

  def addDrawnDevCard( card:DevelopmentCard ):Turn = copy( drawnDevCards = drawnDevCards :+ card )

  def getLastDrawnDevCard:Option[DevelopmentCard] = drawnDevCards.lastOption
}
