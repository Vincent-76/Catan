package com.aimit.htwg.catan.controller

import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.util.Observable

import scala.util.Try

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

  def action( command:Option[Command] ):Try[Option[Info]]
  def undoAction():Try[Option[Info]]
  def redoAction():Try[Option[Info]]

  def saveGame():Try[Option[Info]]

  def loadGame( path:String ):Try[Option[Info]]

  def exit( info:Option[Info] = None ):Try[Option[Info]]

  def initGame():Try[Option[Info]] = action( game.state.initGame() )
  def addPlayer( playerColor:PlayerColor, name:String ):Try[Option[Info]] = action( game.state.addPlayer( playerColor, name ) )
  def setInitBeginnerState():Try[Option[Info]] = action( game.state.setInitBeginnerState() )
  def diceOutBeginner():Try[Option[Info]] = action( game.state.diceOutBeginner() )
  def setBeginner():Try[Option[Info]] = action( game.state.setBeginner() )
  def buildInitSettlement( vID:Int ):Try[Option[Info]] = action( game.state. buildInitSettlement( vID ))
  def buildInitRoad( eID:Int ):Try[Option[Info]] = action( game.state.buildInitRoad( eID ) )
  def startTurn():Try[Option[Info]] = action( game.state.startTurn() )
  def rollTheDices():Try[Option[Info]] = action( game.state.rollTheDices() )
  def useDevCard( devCard:DevelopmentCard ):Try[Option[Info]] = action( game.state.useDevCard( devCard ) )
  def dropResourceCardsToRobber( cards:ResourceCards ):Try[Option[Info]] = action( game.state.dropResourceCardsToRobber( cards ) )
  def placeRobber( hID:Int ):Try[Option[Info]] = action( game.state.placeRobber( hID ) )
  def robberStealFromPlayer( stealPlayerID:PlayerID ):Try[Option[Info]] = action( game.state.robberStealFromPlayer( stealPlayerID ) )
  def setBuildState( structure:StructurePlacement ):Try[Option[Info]] = action( game.state.setBuildState( structure ) )
  def build( id:Int ):Try[Option[Info]] = action( game.state.build( id ) )
  def bankTrade( give:ResourceCards, get:ResourceCards ):Try[Option[Info]] = action( game.state.bankTrade( give, get ) )
  def setPlayerTradeState( give:ResourceCards, get:ResourceCards ):Try[Option[Info]] = action( game.state.setPlayerTradeState( give, get ) )
  def playerTradeDecision( decision:Boolean ):Try[Option[Info]] = action( game.state.playerTradeDecision( decision ) )
  def abortPlayerTrade():Try[Option[Info]] = action( game.state.abortPlayerTrade() )
  def playerTrade( tradePlayerID:PlayerID ):Try[Option[Info]] = action( game.state.playerTrade( tradePlayerID) )
  def buyDevCard():Try[Option[Info]] = action( game.state.buyDevCard() )
  def yearOfPlentyAction( resources:ResourceCards ):Try[Option[Info]] = action( game.state.yearOfPlentyAction( resources ) )
  def devBuildRoad( eID:Int ):Try[Option[Info]] = action( game.state.devBuildRoad( eID ) )
  def monopolyAction( r:Resource ):Try[Option[Info]] = action( game.state.monopolyAction( r ) )
  def endTurn():Try[Option[Info]] = action( game.state.endTurn() )
}
