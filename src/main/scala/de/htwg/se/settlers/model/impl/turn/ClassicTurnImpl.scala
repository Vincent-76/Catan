package de.htwg.se.settlers.model.impl.turn

import de.htwg.se.settlers.model.{ DevelopmentCard, PlayerID, Turn }

case class ClassicTurnImpl( playerIDVal:PlayerID,
                            usedDevCardVal:Boolean = false,
                            drawnDevCards:List[DevelopmentCard] = List.empty ) extends Turn {

  def playerID:PlayerID = playerIDVal

  def usedDevCard:Boolean = usedDevCardVal

  def setUsedDevCard( used:Boolean ):Turn = copy( usedDevCardVal = used )

  def drawnDevCards( devCard:DevelopmentCard ):Int = drawnDevCards.count( _ == devCard )

  def addDrawnDevCard( card:DevelopmentCard ):Turn = copy( drawnDevCards = drawnDevCards :+ card )

  def removeDrawnDevCard( ):Turn = copy( drawnDevCards = drawnDevCards.init )

  def getLastDrawnDevCard:Option[DevelopmentCard] = drawnDevCards.lastOption

  def set( playerID:PlayerID ):Turn = ClassicTurnImpl( playerID )

}
