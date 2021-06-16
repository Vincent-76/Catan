package de.htwg.se.settlers.model.impl.game

import de.htwg.se.settlers.model.Cards._
import de.htwg.se.settlers.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import de.htwg.se.settlers.model.{ BonusCard, Cards, DevStackIsEmpty, DevelopmentCard, Edge, Game, GameField, GreatHallCard, Placement, Player, PlayerColor, PlayerColorIsAlreadyInUse, PlayerID, PlayerNameAlreadyExists, PlayerOrdering, Resource, State, Turn, Vertex }
import de.htwg.se.settlers.model.impl.player.ClassicPlayerImpl
import de.htwg.se.settlers.model.impl.turn.ClassicTurnImpl
import de.htwg.se.settlers.model.state.InitState
import de.htwg.se.settlers.util._

import scala.collection.immutable.{ List, SortedMap, TreeMap }
import scala.util.{ Failure, Random, Success, Try }

/**
 * @author Vincent76;
 */
object ClassicGameImpl {
  val stackResourceAmount:Int = 19
  val testSeed:Int = 1
  val availablePlacements:List[Placement] = List(
    RobberPlacement,
    RoadPlacement,
    SettlementPlacement,
    CityPlacement
  )

  def apply( test:Boolean, gameField:GameField ):ClassicGameImpl = {
    if( test ) new ClassicGameImpl( gameFieldVal = gameField, seedVal = testSeed, developmentCards = Cards.getDevStack( new Random( testSeed ) ) ) else new ClassicGameImpl( gameFieldVal = gameField )
  }

}

case class ClassicGameImpl( gameFieldVal:GameField,
                            stateVal:State = InitState(),
                            resourceStack:ResourceCards = Cards.getResourceCards( ClassicGameImpl.stackResourceAmount ),
                            developmentCards:List[DevelopmentCard] = Cards.getDevStack(),
                            playersVal:SortedMap[PlayerID, Player] = TreeMap.empty[PlayerID, Player]( PlayerOrdering ),
                            turnVal:Turn = ClassicTurnImpl( new PlayerID( -1 ) ),
                            bonusCardsVal:Map[BonusCard, Option[(PlayerID, Int)]] = Cards.bonusCards.map( (_, None) ).toMap,
                            winnerVal:Option[PlayerID] = None,
                            roundVal:Int = 1,
                            seedVal:Int = Random.nextInt( Int.MaxValue / 1000 )
                       ) extends Game {

  def minPlayers:Int = 3
  def maxPlayers:Int = 4
  def requiredVictoryPoints:Int = 10
  def maxHandCards:Int = 7
  def defaultBankTradeFactor:Int = 4
  def unspecifiedPortFactor:Int = 3
  def specifiedPortFactor:Int = 2
  def maxPlayerNameLength:Int = 10
  def availablePlacements:List[Placement] = ClassicGameImpl.availablePlacements

  def gameField:GameField = gameFieldVal
  def state:State = stateVal
  def players:Map[PlayerID, Player] = playersVal
  def turn:Turn = turnVal
  def bonusCards:Map[BonusCard, Option[(PlayerID, Int)]] = bonusCardsVal
  def round:Int = roundVal
  def winner:Option[PlayerID] = winnerVal
  def seed:Int = seedVal

  def bonusCard( bonusCard:BonusCard ):Option[(PlayerID, Int)] = bonusCardsVal( bonusCard )

  def setState( state:State ):ClassicGameImpl = copy( stateVal = state )
  def setGameField( gameField:GameField ):ClassicGameImpl = copy( gameFieldVal = gameField )
  def setResourceStack( resourceStack:ResourceCards ):ClassicGameImpl = copy( resourceStack = resourceStack )
  def setDevelopmentCards( developmentCards:List[DevelopmentCard] ):ClassicGameImpl = copy( developmentCards = developmentCards )
  def updatePlayer( player:Player ):ClassicGameImpl = copy( playersVal = playersVal.updated( player.id, player ) )
  def updatePlayers( updatePlayers:Player* ):ClassicGameImpl = copy( playersVal = updatePlayers.red( playersVal, ( nPlayers:SortedMap[PlayerID, Player], p:Player ) => {
    nPlayers.updated( p.id, p )
  } ) )
  def setTurn( turn:Turn ):ClassicGameImpl = copy( turnVal = turn )
  def setBonusCard( bonusCard:BonusCard, value:Option[(PlayerID, Int)] ):ClassicGameImpl = copy( bonusCardsVal = bonusCardsVal.updated( bonusCard, value ) )
  def setBonusCards( bonusCards:Map[BonusCard, Option[(PlayerID, Int)]] ):ClassicGameImpl = copy( bonusCardsVal = bonusCards )
  def setWinner( winner:PlayerID ):ClassicGameImpl = copy( winnerVal = Some( winner ) )


  def nextRound():ClassicGameImpl = copy( turnVal = turnVal.set( nextTurn() ), roundVal = roundVal + 1 )
  def previousRound( turn:Option[Turn] = None ):ClassicGameImpl = copy( turnVal = turn.getOrElse( turnVal.set( previousTurn() ) ), roundVal = roundVal - 1 )
  def nextTurn( pID:PlayerID = onTurn ):PlayerID = playersVal.keys.find( _.id == pID.id + 1 ).getOrElse( playersVal.firstKey )
  def previousTurn( pID:PlayerID = onTurn ):PlayerID = playersVal.keys.find( _.id == pID.id - 1 ).getOrElse( playersVal.lastKey )



  def addPlayer( playerColor:PlayerColor, name:String ):ClassicGameImpl = {
    val pID = new PlayerID( playersVal.size )
    val p = ClassicPlayerImpl( pID, playerColor, name )
    copy( playersVal = playersVal + (pID -> p ) )
  }

  def addPlayerF( playerColor:PlayerColor, name:String ):Try[ClassicGameImpl] = {
    if( playersVal.exists( _._2.color == playerColor ) )
      Failure( PlayerColorIsAlreadyInUse( playerColor ) )
    else if( playersVal.exists( _._2.name =^ name ) )
      Failure( PlayerNameAlreadyExists( name ) )
    else Success( addPlayer( playerColor, name ) )
  }

  def removeLastPlayer( ):ClassicGameImpl = copy( playersVal = playersVal.init )



  def rollDice( r:Random ):Int = r.nextInt( 6 ) + 1

  def rollDices( ):(Int, Int) = {
    val r = new Random( seedVal * round )
    (rollDice( r ), rollDice( r ))
  }



  def hasStackResources( resources:ResourceCards ):Boolean = resourceStack.has( resources )

  def getAvailableResourceCards( resources:ResourceCards ):(ResourceCards, ResourceCards) = {
    resources.red( (resources, resourceStack), ( cards:(ResourceCards, ResourceCards), r:Resource, amount:Int ) => {
      val available = cards._2.getOrElse( r, 0 )
      if( available >= amount )
        (cards._1, cards._2.updated( r, available - amount ))
      else
        (cards._1.updated( r, available ), cards._2.updated( r, 0 ))
    } )
  }

  /*def getAvailableResourceCards( resources:ResourceCards, stack:ResourceCards = resourceStack ):(ResourceCards, ResourceCards) = {
    resources.red( (resources, stack), ( cards:(ResourceCards, ResourceCards), r:Resource, amount:Int ) => {
      val available = cards._2.getOrElse( r, 0 )
      if( available >= amount )
        (cards._1, cards._2.updated( r, available - amount ))
      else
        (cards._1.updated( r, available ), cards._2.updated( r, 0 ))
    } )
  }*/

  def drawResourceCards( pID:PlayerID, r:Resource, amount:Int = 1 ):(ClassicGameImpl, ResourceCards) =
    drawResourceCards( pID, ResourceCards.ofResource( r, amount ) )

  def drawResourceCards( pID:PlayerID, cards:ResourceCards ):(ClassicGameImpl, ResourceCards) = {
    val (available, newStack) = getAvailableResourceCards( cards )
    val newGame = if( available.amount > 0 ) {
      val newPlayer = playersVal( pID ).addResourceCards( available )
      copy(
        playersVal = playersVal.updated( newPlayer.id, newPlayer ),
        resourceStack = newStack
      )
    } else this
    (newGame, available)
  }

  def dropResourceCards( pID:PlayerID, r:Resource, amount:Int = 1 ):Try[ClassicGameImpl] =
    dropResourceCards( pID, ResourceCards.ofResource( r, amount ) )

  def dropResourceCards( pID:PlayerID, cards:ResourceCards ):Try[ClassicGameImpl] = {
    playersVal( pID ).removeResourceCards( cards ) match {
      case Success( nPlayer ) => Success( copy(
        playersVal = playersVal.updated( pID, nPlayer ),
        resourceStack = resourceStack.add( cards )
      ) )
      case f => f.rethrow
    }
  }

  def drawDevCard( pID:PlayerID ):Try[ClassicGameImpl] = {
    if( developmentCards.isEmpty )
      Failure( DevStackIsEmpty )
    else dropResourceCards( pID, Cards.developmentCardCost ) match {
      case Success( newGame ) =>
        val newPlayer = newGame.playersVal( pID ).addDevCard( developmentCards.head )
        Success( newGame.copy(
          playersVal = newGame.playersVal.updated( pID, newPlayer ),
          turnVal = turn.addDrawnDevCard( developmentCards.head ),
          developmentCards = developmentCards.tail
        ) )
      case Failure( e ) => Failure( e )
    }
  }

  def addDevCard( devCard:DevelopmentCard ):ClassicGameImpl = copy( developmentCards = devCard +: developmentCards )



  def getPlayerBonusCards( pID:PlayerID ):Iterable[BonusCard] =
    bonusCardsVal.filter( d => d._2.isDefined && d._2.get._1 == pID ).keys

  def getPlayerDisplayVictoryPoints( pID:PlayerID ):Int =
    getPlayerBonusCards( pID ).red( player( pID ).victoryPoints, ( points:Int, bonusCard:BonusCard ) => points + bonusCard.bonus )

  def getPlayerVictoryPoints( pID:PlayerID ):Int = {
    getPlayerDisplayVictoryPoints( pID ) + player( pID ).devCards.count( _ == GreatHallCard )
  }

  def getBankTradeFactor( playerID:PlayerID, r:Resource ):Int =
    gameField.vertexList.red( defaultBankTradeFactor, ( factor:Int, v:Vertex ) => {
      if( v.building.isDefined && v.building.get.owner == playerID && v.port.isDefined )
        if( v.port.get.specific.isDefined && v.port.get.specific.get == r )
          specifiedPortFactor
        else if( v.port.get.specific.isEmpty && unspecifiedPortFactor < factor )
          unspecifiedPortFactor
        else factor
      else factor
    } )



  def roadBuildable( edge:Edge, pID:PlayerID ):Boolean = (playerHasAdjacentEdge( pID, gameField.adjacentEdges( edge ) ) ||
    playerHasAdjacentVertex( pID, gameField.adjacentVertices( edge ) )) && (edge.h1.isLand || edge.h2.isLand)

  private def roadLength( pID:PlayerID, e:Edge, count:Int = 1, previous:List[Edge] = List.empty ):Int = {
    val lengths = gameField.adjacentEdges( e )
      .filter( e => e.road.isDefined && e.road.get.owner == pID && !previous.contains( e ) )
      .map( e2 => roadLength( pID, e2, count + 1, previous :+ e ) )
    if( lengths.isEmpty )
      count
    else lengths.max
  }

  def getLongestRoadLength( pID:PlayerID ):Int = {
    val lengths = gameField.edgeList.filter( e => e.road.isDefined && e.road.get.owner == pID ).map( e => roadLength( pID, e ) )
    if( lengths.nonEmpty ) lengths.max else 0
  }

  def getBuildableRoadSpotsForSettlement( vID:Int ):List[Edge] = {
    val vertex = gameField.findVertex( vID )
    if( vertex.isDefined && vertex.get.building.isDefined )
      gameField.adjacentEdges( vertex.get ).filter( e => (e.h1.isLand || e.h2.isLand) && e.road.isEmpty )
    else List.empty
  }



  def noBuildingInRange( v:Vertex ):Boolean = {
    gameField.adjacentEdges( v ).foreach( e1 => {
      gameField.adjacentVertices( e1 ).filter( _ != v ).foreach( v1 => {
        if( v1.building.nonEmpty )
          return false
      } )
    } )
    true
  }
}
