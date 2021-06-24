package de.htwg.se.catan.model

import Cards.ResourceCards
import de.htwg.se.catan.model.impl.fileio.{ XMLParseError, XMLSerializable }
import de.htwg.se.catan.model.state._

import scala.xml.Node

/**
 * @author Vincent76;
 */
object State {
  def fromXML( node:Node ):State = node.label match {
    case "ActionState" => ActionState.fromXML( node )
    case "BuildInitRoadState" => BuildInitRoadState.fromXML( node )
    case "BuildInitSettlementState" => BuildInitSettlementState.fromXML( node )
    case "BuildState" => BuildState.fromXML( node )
    case "DevRoadBuildingState" => DevRoadBuildingState.fromXML( node )
    case "DiceState" => DiceState.fromXML( node )
    case "DropHandCardsState" => DropHandCardsState.fromXML( node )
    case "InitBeginnerState" => InitBeginnerState.fromXML( node )
    case "InitPlayerState" => InitPlayerState.fromXML( node )
    case "InitState" => InitState.fromXML( node )
    case "MonopolyState" => MonopolyState.fromXML( node )
    case "NextPlayerState" => NextPlayerState.fromXML( node )
    case "PlayerTradeEndState" => PlayerTradeEndState.fromXML( node )
    case "PlayerTradeState" => PlayerTradeState.fromXML( node )
    case "RobberPlaceState" => RobberPlaceState.fromXML( node )
    case "RobberStealState" => RobberStealState.fromXML( node )
    case "YearOfPlentyState" => YearOfPlentyState.fromXML( node )
    case e => throw XMLParseError( expected = "State", got = e )
  }
}

trait State extends XMLSerializable {

  def initGame( ):Option[Command] = None

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
