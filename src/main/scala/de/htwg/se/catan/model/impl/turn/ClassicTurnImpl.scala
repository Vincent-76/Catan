package de.htwg.se.catan.model.impl.turn

import com.google.inject.Inject
import de.htwg.se.catan.model.{ DevelopmentCard, PlayerID, Turn }

case class ClassicTurnImpl( playerIDVal:PlayerID = new PlayerID( -1 ),
                            usedDevCardVal:Boolean = false,
                            drawnDevCardsVal:List[DevelopmentCard] = List.empty ) extends Turn {

  @Inject
  def this( ) = this( new PlayerID( -1 ) )

  def playerID:PlayerID = playerIDVal

  def usedDevCard:Boolean = usedDevCardVal

  def setUsedDevCard( used:Boolean ):Turn = copy( usedDevCardVal = used )

  def drawnDevCards:List[DevelopmentCard] = drawnDevCardsVal

  def addDrawnDevCard( card:DevelopmentCard ):Turn = copy( drawnDevCardsVal = drawnDevCardsVal :+ card )

  def removeDrawnDevCard( ):Turn = copy( drawnDevCardsVal = drawnDevCardsVal.init )

  def getLastDrawnDevCard:Option[DevelopmentCard] = drawnDevCardsVal.lastOption

  def set( playerID:PlayerID ):Turn = ClassicTurnImpl( playerID )

}
