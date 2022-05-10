package de.htwg.se.catan.controller

import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.*
import de.htwg.se.catan.util.Observable

import scala.util.Try
import scala.xml.Node

/**
 * @author Vincent76;
 */

trait Controller extends Observable:

  def game:Game

  def onTurn:PlayerID = game.onTurn
  def player:Player = game.player( onTurn )
  def player( pID:PlayerID ):Player = game.player( pID )

  def hasUndo:Boolean
  def hasRedo:Boolean

  def action( command:Option[Command] ):Try[ActionResult]
  def undoAction():Try[ActionResult]
  def redoAction():Try[ActionResult]

  def saveGame():String

  def loadGame( path:String ):Try[ActionResult]

  def exit( info:Option[Info] = None ):Unit

  def initGame():Try[ActionResult] = action( game.state.initGame() )
  def addPlayer( playerColor:PlayerColor, name:String ):Try[ActionResult] = action( game.state.addPlayer( playerColor, name ) )
  def setInitBeginnerState():Try[ActionResult] = action( game.state.setInitBeginnerState() )
  def diceOutBeginner():Try[ActionResult] = action( game.state.diceOutBeginner() )
  def setBeginner():Try[ActionResult] = action( game.state.setBeginner() )
  def buildInitSettlement( vID:Int ):Try[ActionResult] = action( game.state. buildInitSettlement( vID ))
  def buildInitRoad( eID:Int ):Try[ActionResult] = action( game.state.buildInitRoad( eID ) )
  def startTurn():Try[ActionResult] = action( game.state.startTurn() )
  def rollTheDices():Try[ActionResult] = action( game.state.rollTheDices() )
  def useDevCard( devCard:DevelopmentCard ):Try[ActionResult] = action( game.state.useDevCard( devCard ) )
  def dropResourceCardsToRobber( cards:ResourceCards ):Try[ActionResult] = action( game.state.dropResourceCardsToRobber( cards ) )
  def placeRobber( hID:Int ):Try[ActionResult] = action( game.state.placeRobber( hID ) )
  def robberStealFromPlayer( stealPlayerID:PlayerID ):Try[ActionResult] = action( game.state.robberStealFromPlayer( stealPlayerID ) )
  def setBuildState( structure:StructurePlacement ):Try[ActionResult] = action( game.state.setBuildState( structure ) )
  def build( id:Int ):Try[ActionResult] = action( game.state.build( id ) )
  def bankTrade( give:ResourceCards, get:ResourceCards ):Try[ActionResult] = action( game.state.bankTrade( give, get ) )
  def setPlayerTradeState( give:ResourceCards, get:ResourceCards ):Try[ActionResult] = action( game.state.setPlayerTradeState( give, get ) )
  def playerTradeDecision( decision:Boolean ):Try[ActionResult] = action( game.state.playerTradeDecision( decision ) )
  def abortPlayerTrade():Try[ActionResult] = action( game.state.abortPlayerTrade() )
  def playerTrade( tradePlayerID:PlayerID ):Try[ActionResult] = action( game.state.playerTrade( tradePlayerID) )
  def buyDevCard():Try[ActionResult] = action( game.state.buyDevCard() )
  def yearOfPlentyAction( resources:ResourceCards ):Try[ActionResult] = action( game.state.yearOfPlentyAction( resources ) )
  def devBuildRoad( eID:Int ):Try[ActionResult] = action( game.state.devBuildRoad( eID ) )
  def monopolyAction( r:Resource ):Try[ActionResult] = action( game.state.monopolyAction( r ) )
  def endTurn():Try[ActionResult] = action( game.state.endTurn() )
