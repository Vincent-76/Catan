package de.htwg.se.settlers.model

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID

/**
 * @author Vincent76;
 */
abstract class State( controller:Controller ) {

  def show():Unit = controller.ui.show()

  def onError( t:Throwable ):Unit = controller.ui.onError( t )

  def initPlayers( ):Unit = onError( WrongState )

  def addPlayer( playerColor:PlayerColor, name:String ):Unit = onError( WrongState )

  def setInitBeginnerState():Unit = onError( WrongState )

  def diceOutBeginner():Unit = onError( WrongState )

  def buildInitSettlement( vID:Int ):Unit = onError( WrongState )

  def buildInitRoad( eID:Int ):Unit = onError( WrongState )

  def startTurn():Unit = onError( WrongState )

  def rollTheDices():Unit = onError( WrongState )

  def useDevCard( devCard:DevelopmentCard ):Unit = onError( WrongState )

  def dropResourceCardsToRobber( cards:ResourceCards ):Unit = onError( WrongState )

  def placeRobber( hID:Int ):Unit = onError( WrongState )

  def robberStealFromPlayer( stealPlayerID:PlayerID ):Unit = onError( WrongState )

  def setBuildState( structure:StructurePlacement ):Unit = onError( WrongState )

  def build( id:Int ):Unit = onError( WrongState )

  def bankTrade( give:(Resource, Int), get:(Resource, Int) ):Unit = onError( WrongState )

  def setPlayerTradeState( give:ResourceCards, get:ResourceCards ):Unit = onError( WrongState )

  def playerTradeDecision( decision:Boolean ):Unit = onError( WrongState )

  def abortPlayerTrade():Unit = onError( WrongState )

  def playerTrade( tradePlayerID:PlayerID ):Unit = onError( WrongState )

  def buyDevCard():Unit = onError( WrongState )

  def yearOfPlentyAction( resources:ResourceCards ):Unit = onError( WrongState )

  def devBuildRoad( eID:Int ):Unit = onError( WrongState )

  def monopolyAction( r:Resource ):Unit = onError( WrongState )

  def endTurn():Unit = onError( WrongState )


  def exit():Unit = controller.exit()

  def undo():Unit = controller.undoAction()

  def redo():Unit = controller.redoAction()
}
