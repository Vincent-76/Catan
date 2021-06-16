package de.htwg.se.catan.model

/**
 * @author Vincent76;
 */
trait Turn {

  def playerID:PlayerID

  def usedDevCard:Boolean

  def setUsedDevCard( used:Boolean ):Turn

  def drawnDevCards:List[DevelopmentCard]

  def addDrawnDevCard( card:DevelopmentCard ):Turn

  def removeDrawnDevCard( ):Turn

  def getLastDrawnDevCard:Option[DevelopmentCard]

  def set( playerID:PlayerID ):Turn
}
