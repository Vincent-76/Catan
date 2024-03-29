package com.aimit.htwg.catan.controller

import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.util.Observable

/**
 * @author Vincent76;
 */

trait Controller extends Observable {

  def game:Game

  def onTurn:PlayerID = game.onTurn
  def player:Player = game.player( onTurn )
  def player( pID:PlayerID ):Player = game.player( pID )

  def hasUndo:Boolean
  def hasRedo:Boolean

  def action( command:Option[Command] ):Unit
  def undoAction():Unit
  def redoAction():Unit

  def saveGame():String

  def loadGame( path:String ):Unit

  def exit( info:Option[Info] = None ):Unit

  def initGame():Unit = action( game.state.initGame() )
  def addPlayer( playerColor:PlayerColor, name:String ):Unit = action( game.state.addPlayer( playerColor, name ) )
  def setInitBeginnerState():Unit = action( game.state.setInitBeginnerState() )
  def diceOutBeginner():Unit = action( game.state.diceOutBeginner() )
  def setBeginner():Unit = action( game.state.setBeginner() )
  def buildInitSettlement( vID:Int ):Unit = action( game.state. buildInitSettlement( vID ))
  def buildInitRoad( eID:Int ):Unit = action( game.state.buildInitRoad( eID ) )
  def startTurn():Unit = action( game.state.startTurn() )
  def rollTheDices():Unit = action( game.state.rollTheDices() )
  def useDevCard( devCard:DevelopmentCard ):Unit = action( game.state.useDevCard( devCard ) )
  def dropResourceCardsToRobber( cards:ResourceCards ):Unit = action( game.state.dropResourceCardsToRobber( cards ) )
  def placeRobber( hID:Int ):Unit = action( game.state.placeRobber( hID ) )
  def robberStealFromPlayer( stealPlayerID:PlayerID ):Unit = action( game.state.robberStealFromPlayer( stealPlayerID ) )
  def setBuildState( structure:StructurePlacement ):Unit = action( game.state.setBuildState( structure ) )
  def build( id:Int ):Unit = action( game.state.build( id ) )
  def bankTrade( give:ResourceCards, get:ResourceCards ):Unit = action( game.state.bankTrade( give, get ) )
  def setPlayerTradeState( give:ResourceCards, get:ResourceCards ):Unit = action( game.state.setPlayerTradeState( give, get ) )
  def playerTradeDecision( decision:Boolean ):Unit = action( game.state.playerTradeDecision( decision ) )
  def abortPlayerTrade():Unit = action( game.state.abortPlayerTrade() )
  def playerTrade( tradePlayerID:PlayerID ):Unit = action( game.state.playerTrade( tradePlayerID) )
  def buyDevCard():Unit = action( game.state.buyDevCard() )
  def yearOfPlentyAction( resources:ResourceCards ):Unit = action( game.state.yearOfPlentyAction( resources ) )
  def devBuildRoad( eID:Int ):Unit = action( game.state.devBuildRoad( eID ) )
  def monopolyAction( r:Resource ):Unit = action( game.state.monopolyAction( r ) )
  def endTurn():Unit = action( game.state.endTurn() )
}
