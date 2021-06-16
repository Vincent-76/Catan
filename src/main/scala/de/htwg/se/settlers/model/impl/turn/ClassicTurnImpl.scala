package de.htwg.se.settlers.model.impl.turn

import de.htwg.se.settlers.model.{ DevelopmentCard, PlayerID, Turn }

case class ClassicTurnImpl( playerIDVal:PlayerID,
                            usedDevCardVal:Boolean = false,
                            drawnDevCardsVal:List[DevelopmentCard] = List.empty ) extends Turn {

  def playerID:PlayerID = playerIDVal

  def usedDevCard:Boolean = usedDevCardVal

  def setUsedDevCard( used:Boolean ):Turn = copy( usedDevCardVal = used )

  def drawnDevCards:List[DevelopmentCard] = drawnDevCardsVal

  def addDrawnDevCard( card:DevelopmentCard ):Turn = copy( drawnDevCardsVal = drawnDevCardsVal :+ card )

  def removeDrawnDevCard( ):Turn = copy( drawnDevCardsVal = drawnDevCardsVal.init )

  def getLastDrawnDevCard:Option[DevelopmentCard] = drawnDevCardsVal.lastOption

  def set( playerID:PlayerID ):Turn = ClassicTurnImpl( playerID )

}
