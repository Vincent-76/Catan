package de.htwg.se.settlers.model

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.Player.PlayerColor

/**
 * @author Vincent76;
 */
abstract class State( controller:Controller ) {

  def initPlayers( ):Unit = controller.error( WrongState )

  def addPlayer( playerColor:PlayerColor, name:String ):Unit = controller.error( WrongState )

  def setInitBeginnerState():Unit = controller.error( WrongState )

  def diceOutBeginner():Unit = controller.error( WrongState )

  def setBeginner():Unit = controller.error( WrongState )

  def buildInitSettlement( vID:Int ):Unit = controller.error( WrongState )

  def buildInitRoad( eID:Int ):Unit = controller.error( WrongState )

  def startTurn():Unit = controller.error( WrongState )

  def rollTheDices():Unit = controller.error( WrongState )

  def useDevCard( devCard:DevelopmentCard ):Unit = controller.error( WrongState )

  def dropResourceCardsToRobber( cards:ResourceCards ):Unit = controller.error( WrongState )

  def placeRobber( hID:Int ):Unit = controller.error( WrongState )

  def robberStealFromPlayer( stealPlayerID:PlayerID ):Unit = controller.error( WrongState )

  def setBuildState( structure:StructurePlacement ):Unit = controller.error( WrongState )

  def build( id:Int ):Unit = controller.error( WrongState )

  def bankTrade( give:ResourceCards, get:ResourceCards ):Unit = controller.error( WrongState )

  def setPlayerTradeState( give:ResourceCards, get:ResourceCards ):Unit = controller.error( WrongState )

  def playerTradeDecision( decision:Boolean ):Unit = controller.error( WrongState )

  def abortPlayerTrade():Unit = controller.error( WrongState )

  def playerTrade( tradePlayerID:PlayerID ):Unit = controller.error( WrongState )

  def buyDevCard():Unit = controller.error( WrongState )

  def yearOfPlentyAction( resources:ResourceCards ):Unit = controller.error( WrongState )

  def devBuildRoad( eID:Int ):Unit = controller.error( WrongState )

  def monopolyAction( r:Resource ):Unit = controller.error( WrongState )

  def endTurn():Unit = controller.error( WrongState )


  def exit():Unit = controller.exit()

  def undo():Unit = controller.undoAction()

  def redo():Unit = controller.redoAction()
}
