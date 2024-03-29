package com.aimit.htwg.catan.model.impl.game

import com.google.inject.Inject
import com.google.inject.name.Named
import com.aimit.htwg.catan.CatanModule
import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO._
import com.aimit.htwg.catan.model.impl.fileio.JsonSerializable
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode, XMLNodeSeq, XMLOption, XMLSequence, XMLTuple2 }
import com.aimit.htwg.catan.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import com.aimit.htwg.catan.model.state.InitState
import com.aimit.htwg.catan.util._
import play.api.libs.json.{ JsResult, JsSuccess, JsValue, Json, Reads, Writes }

import scala.collection.immutable.{ List, SortedMap, TreeMap }
import scala.util.{ Failure, Random, Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */
object ClassicGameImpl extends GameImpl( "ClassicGameImpl" ) {

  val stackResourceAmount:Int = 19
  val availablePlacements:List[Placement] = List(
    RobberPlacement,
    RoadPlacement,
    SettlementPlacement,
    CityPlacement
  )

  def fromXML( node:Node ):ClassicGameImpl = ClassicGameImpl(
    gameFieldVal = GameField.fromXML( node.childOf( "gameField" ) ),
    turnVal = Turn.fromXML( node.childOf( "turn" ) ),
    seedVal = (node \ "@seed").content.toInt,
    playerFactory = CatanModule.playerFactoryFromString( (node \ "@playerFactoryClass").content ).get,
    playerFactoryClass = (node \ "@playerFactoryClass").content,
    availablePlacementsVal = node.childOf( "availablePlacements" ).asList( n => Placement.of( n.content ).get ),
    stateVal = State.fromXML( node.childOf( "state" ) ),
    resourceStack = ResourceCards.fromXML( node.childOf( "resourceStack" ) ),
    developmentCards = node.childOf( "developmentCards" ).asList( n => DevelopmentCard.of( n.content ).get ),
    playersVal = TreeMap(
      node.childOf( "players" ).asMap(
        n => PlayerID.fromXML( n ),
        n => Player.fromXML( n )
      ).toArray:_*
    )( PlayerOrdering ),
    bonusCardsVal = node.childOf( "bonusCards" ).asMap(
      n => BonusCard.of( n.content ).get,
      _.asOption( _.asTuple(
        n => PlayerID.fromXML( n ),
        _.content.toInt
      ) )
    ),
    winnerVal = node.childOf( "winner" ).asOption( n => PlayerID.fromXML( n ) ),
    roundVal = (node \ "@round").content.toInt
  )

  def fromJson( json:JsValue ):ClassicGameImpl = ClassicGameImpl(
    gameFieldVal = ( json \ "gameField" ).as[GameField],
    turnVal = ( json \ "turn" ).as[Turn],
    seedVal = ( json \ "seed" ).as[Int],
    playerFactory = CatanModule.playerFactoryFromString( ( json \ "playerFactoryClass" ).as[String] ).get,
    playerFactoryClass = ( json \ "playerFactoryClass" ).as[String],
    availablePlacementsVal = ( json \ "availablePlacements" ).asList[Placement],
    stateVal = ( json \ "state" ).as[State],
    resourceStack = ( json \ "resourceStack" ).as[ResourceCards],
    developmentCards = ( json \ "developmentCards"  ).asList[DevelopmentCard],
    playersVal = TreeMap( ( json \ "players" ).asMap[PlayerID, Player].toArray:_* )( PlayerOrdering ),
    bonusCardsVal = ( json \ "bonusCards" ).asMapC( _.as[BonusCard], _.asOptionC( _.asTuple[PlayerID, Int] ) ),
    winnerVal = ( json \ "winner" ).asOpt[PlayerID],
    roundVal = ( json \ "round" ).as[Int]
  )
}

case class ClassicGameImpl( gameFieldVal:GameField,
                            turnVal:Turn,
                            seedVal:Int,
                            playerFactory:PlayerFactory,
                            playerFactoryClass:String,
                            availablePlacementsVal:List[Placement],
                            stateVal:State = InitState(),
                            resourceStack:ResourceCards = Card.getResourceCards( ClassicGameImpl.stackResourceAmount ),
                            developmentCards:List[DevelopmentCard] = List.empty,
                            playersVal:SortedMap[PlayerID, Player] = TreeMap.empty[PlayerID, Player]( PlayerOrdering ),
                            bonusCardsVal:Map[BonusCard, Option[(PlayerID, Int)]] = BonusCard.impls.map( (_, None) ).toMap,
                            winnerVal:Option[PlayerID] = None,
                            roundVal:Int = 1
                          ) extends Game {

  @Inject
  def this(
            gameField:GameField,
            turn:Turn,
            @Named( "seed" ) seed:Int,
            playerFactory:PlayerFactory,
            @Named( "playerFactoryClass" ) playerFactoryClass:String,
            @Named( "availablePlacements" ) availablePlacements:List[Placement]
          ) = this(
    gameFieldVal = gameField,
    turnVal = turn,
    seedVal = seed,
    playerFactory = playerFactory,
    playerFactoryClass = playerFactoryClass,
    availablePlacementsVal = ClassicGameImpl.availablePlacements.filter( availablePlacements.contains ),
    developmentCards = DevelopmentCard.getStack( new Random( seed ) ),
  )

  def this( gameField:GameField, turn:Turn, seed:Int, playerFactory:PlayerFactory, playerFactoryClass:String ) =
    this( gameField, turn, seed, playerFactory, playerFactoryClass, ClassicGameImpl.availablePlacements )


  def toXML:Node = <ClassicGameImpl seed={seedVal.toString} round={roundVal.toString} playerFactoryClass={playerFactoryClass}>
    <turn>{turnVal.toXML}</turn>
    <availablePlacements>{availablePlacementsVal.toXML( _.title )}</availablePlacements>
    <state>{stateVal.toXML}</state>
    <resourceStack>{resourceStack.toXML( _.title, _.toString )}</resourceStack>
    <developmentCards>{developmentCards.toXML( _.title )}</developmentCards>
    <players>{playersVal.toXML( _.toXML, _.toXML )}</players>
    <bonusCards>{bonusCardsVal.toXML( _.title, _.toXML( _.toXML( _.toXML, _.toString ) ) )}</bonusCards>
    <winner>{winnerVal.toXML( _.toXML )}</winner>
    <gameField>{gameFieldVal.toXML}</gameField>
  </ClassicGameImpl>.copy( label = ClassicGameImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( ClassicGameImpl.name ),
    "gameField" -> Json.toJson( gameFieldVal.asInstanceOf[JsonSerializable] ),
    "turn" -> Json.toJson( turnVal.asInstanceOf[JsonSerializable] ),
    "seed" -> Json.toJson( seedVal ),
    "playerFactoryClass" -> Json.toJson( playerFactoryClass ),
    "availablePlacements" -> Json.toJson( availablePlacements ),
    "state" -> Json.toJson( stateVal.asInstanceOf[JsonSerializable] ),
    "resourceStack" -> Json.toJson( resourceStack ),
    "developmentCards" -> Json.toJson( developmentCards ),
    "players" -> Json.toJson( playersVal.asInstanceOf[Map[PlayerID, JsonSerializable]] ),
    "bonusCards" ->  Json.toJson( bonusCardsVal ),
    "winner" -> Json.toJson( winnerVal ),
    "round" -> Json.toJson( roundVal )
  )


  def minPlayers:Int = 3

  def maxPlayers:Int = 4

  def requiredVictoryPoints:Int = 10

  def maxHandCards:Int = 7

  def defaultBankTradeFactor:Int = 4

  def unspecifiedPortFactor:Int = 3

  def specifiedPortFactor:Int = 2

  def maxPlayerNameLength:Int = 10

  def availablePlacements:List[Placement] = availablePlacementsVal

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


  def nextRound( ):ClassicGameImpl = copy( turnVal = turnVal.set( nextTurn() ), roundVal = roundVal + 1 )

  def previousRound( turn:Option[Turn] = None ):ClassicGameImpl = copy( turnVal = turn.getOrElse( turnVal.set( previousTurn() ) ), roundVal = roundVal - 1 )

  def nextTurn( pID:PlayerID = onTurn ):PlayerID = playersVal.keys.find( _.id == pID.id + 1 ).getOrElse( playersVal.firstKey )

  def previousTurn( pID:PlayerID = onTurn ):PlayerID = playersVal.keys.find( _.id == pID.id - 1 ).getOrElse( playersVal.lastKey )


  def addPlayer( playerColor:PlayerColor, name:String ):ClassicGameImpl = {
    val pID = new PlayerID( playersVal.size )
    //val p = ClassicPlayerImpl( pID, playerColor, name )
    val p = playerFactory.create( pID, playerColor, name )
    copy( playersVal = playersVal + (pID -> p) )
  }

  def addPlayerF( playerColor:PlayerColor, name:String ):Try[ClassicGameImpl] = {
    if( playersVal.exists( _._2.color == playerColor ) )
      Failure( PlayerColorIsAlreadyInUse( playerColor ) )
    else if( playersVal.exists( _._2.name ^= name ) )
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
    else dropResourceCards( pID, DevelopmentCard.cardCost ) match {
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
