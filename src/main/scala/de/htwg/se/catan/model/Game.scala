package de.htwg.se.catan.model

import de.htwg.se.catan.model.Cards._
import de.htwg.se.catan.model.impl.fileio.XMLSerializable
import de.htwg.se.catan.util._

import scala.collection.immutable.List
import scala.util.{ Random, Try }

/**
 * @author Vincent76;
 */

object PlayerOrdering extends Ordering[PlayerID] {
  override def compare( x:PlayerID, y:PlayerID ):Int = x.id.compareTo( y.id )
}

trait Game extends XMLSerializable {

  def minPlayers:Int
  def maxPlayers:Int
  def requiredVictoryPoints:Int
  def maxHandCards:Int
  def defaultBankTradeFactor:Int
  def unspecifiedPortFactor:Int
  def specifiedPortFactor:Int
  def maxPlayerNameLength:Int
  def availablePlacements:List[Placement]

  def gameField:GameField
  def state:State
  def players:Map[PlayerID, Player]
  def turn:Turn
  def bonusCards:Map[BonusCard, Option[(PlayerID, Int)]]
  def round:Int
  def winner:Option[PlayerID]
  def seed:Int

  def bonusCard( bonusCard:BonusCard ):Option[(PlayerID, Int)]

  /*def update(
            state:State = state,
            gameField:GameField = gameField,
            resourceStack:ResourceCards = resourceStack,
            //developmentCards:List[DevelopmentCard] = developmentCards,
            players:SortedMap[PlayerID, Player] = players,
            turn:Turn = turn,
            bonusCards:Map[BonusCard, Option[(PlayerID, Int)]] = bonusCards,
            round:Int = round,
            winner:Option[PlayerID] = winner
          ):Game*/


  def setState( state:State ):Game
  def setGameField( gameField:GameField ):Game
  def setResourceStack( resourceStack:ResourceCards ):Game
  def setDevelopmentCards( developmentCards:List[DevelopmentCard] ):Game
  def updatePlayer( player:Player ):Game
  def updatePlayers( updatePlayers:Player* ):Game
  def setTurn( turn:Turn ):Game
  def setBonusCard( bonusCard:BonusCard, value:Option[(PlayerID, Int)] ):Game
  def setBonusCards( bonusCards:Map[BonusCard, Option[(PlayerID, Int)]] ):Game
  def setWinner( winner:PlayerID ):Game

  def nextRound():Game
  def previousRound( turn:Option[Turn] = None ):Game
  def onTurn:PlayerID = turn.playerID
  def player:Player = player( onTurn )
  def player( pID:PlayerID ):Player = players( pID )
  def isPlayer( playerID:Int ):Boolean = players.keys.exists( _.id == playerID )
  def getPlayerID( playerID:Int ):Option[PlayerID] = players.keys.find( _.id == playerID )

  def nextTurn( pID:PlayerID = onTurn ):PlayerID
  def previousTurn( pID:PlayerID = onTurn ):PlayerID
  def nextPlayer( p:Player = player ):Player = players( nextTurn( p.id ) )
  def previousPlayer( p:Player = player ):Player = players( previousTurn( p.id ) )

  def addPlayer( playerColor:PlayerColor, name:String ):Game
  def addPlayerF( playerColor:PlayerColor, name:String ):Try[Game]
  def removeLastPlayer( ):Game

  def rollDice( r:Random ):Int
  def rollDices( ):(Int, Int)

  def hasStackResources( resources:ResourceCards ):Boolean
  def getAvailableResourceCards( resources:ResourceCards ):(ResourceCards, ResourceCards)
  //def getAvailableResourceCards( resources:ResourceCards, stack:ResourceCards ):(ResourceCards, ResourceCards)
  def drawResourceCards( pID:PlayerID, r:Resource, amount:Int = 1 ):(Game, ResourceCards)
  def drawResourceCards( pID:PlayerID, cards:ResourceCards ):(Game, ResourceCards)
  def dropResourceCards( pID:PlayerID, r:Resource, amount:Int = 1 ):Try[Game]
  def dropResourceCards( pID:PlayerID, cards:ResourceCards ):Try[Game]
  def drawDevCard( pID:PlayerID ):Try[Game]
  def addDevCard( devCard:DevelopmentCard ):Game

  def getPlayerBonusCards( pID:PlayerID ):Iterable[BonusCard]
  def getPlayerDisplayVictoryPoints( pID:PlayerID ):Int
  def getPlayerVictoryPoints( pID:PlayerID ):Int
  def getBankTradeFactor( playerID:PlayerID, r:Resource ):Int

  def settlementAmount( pID:PlayerID ):Int = gameField.vertexList.count( v => v.building.isDefined && v.building.get.owner == pID )
  def roadAmount( pID:PlayerID ):Int = gameField.edgeList.count( e => e.road.isDefined && e.road.get.owner == pID )
  def roadBuildable( edge:Edge, pID:PlayerID ):Boolean
  def getLongestRoadLength( pID:PlayerID ):Int
  def getBuildableRoadSpotsForSettlement( vID:Int ):List[Edge]

  def noBuildingInRange( v:Vertex ):Boolean
  def playerHasAdjacentEdge( pID:PlayerID, edges:List[Edge] ):Boolean = edges.containsWhere( e => e.road.isDefined && e.road.get.owner == pID )
  def playerHasAdjacentVertex( pID:PlayerID, vertices:List[Vertex] ):Boolean = vertices.containsWhere( v => v.building.isDefined && v.building.get.owner == pID )

  def checkHandCardsInOrder( p:Player = player, dropped:List[PlayerID] = List.empty ):Option[Player] = {
    if( !dropped.contains( p.id ) && p.resourceAmount > maxHandCards )
      Some( p )
    else nextPlayer( p ) match {
      case next:Player if next.id == onTurn => None
      case next:Player => checkHandCardsInOrder( next, dropped )
    }
  }

  def getNextTradePlayerInOrder( decisions:Map[PlayerID, Boolean], pID:PlayerID = nextTurn() ):Option[PlayerID] =
    getNextTradePlayerInOrder( decisions, players( pID ) )

  def getNextTradePlayerInOrder( decisions:Map[PlayerID, Boolean], p:Player ):Option[PlayerID] = {
    if( p == player )
      None
    else if( decisions.contains( p.id ) )
      getNextTradePlayerInOrder( decisions, nextPlayer( p ) )
    else Some( p.id )
  }
}