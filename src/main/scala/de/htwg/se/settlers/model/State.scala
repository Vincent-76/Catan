package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.Player.PlayerColor

/**
 * @author Vincent76;
 */
abstract class State {

  def initPlayers( ):Option[Command] = None

  def addPlayer( playerColor:PlayerColor, name:String ):Option[Command] = None

  def setInitBeginnerState():Option[Command] = None

  def diceOutBeginner():Option[Command] = None

  def setBeginner():Option[Command] = None

  def buildInitSettlement( vID:Int ):Option[Command] = None

  def buildInitRoad( eID:Int ):Option[Command] = None

  def startTurn():Option[Command] = None

  def rollTheDices():Option[Command] = None

  def useDevCard( devCard:DevelopmentCard ):Option[Command] = None

  def dropResourceCardsToRobber( cards:ResourceCards ):Option[Command] = None

  def placeRobber( hID:Int ):Option[Command] = None

  def robberStealFromPlayer( stealPlayerID:PlayerID ):Option[Command] = None

  def setBuildState( structure:StructurePlacement ):Option[Command] = None

  def build( id:Int ):Option[Command] = None

  def bankTrade( give:ResourceCards, get:ResourceCards ):Option[Command] = None

  def setPlayerTradeState( give:ResourceCards, get:ResourceCards ):Option[Command] = None

  def playerTradeDecision( decision:Boolean ):Option[Command] = None

  def abortPlayerTrade():Option[Command] = None

  def playerTrade( tradePlayerID:PlayerID ):Option[Command] = None

  def buyDevCard():Option[Command] = None

  def yearOfPlentyAction( resources:ResourceCards ):Option[Command] = None

  def devBuildRoad( eID:Int ):Option[Command] = None

  def monopolyAction( r:Resource ):Option[Command] = None

  def endTurn():Option[Command] = None
}
