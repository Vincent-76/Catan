package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.GameField.{ Edge, Hex, Row, Vertex }
import de.htwg.se.settlers.util._

import scala.util.{ Success, Failure, Try }

/**
 * @author Vincent76;
 */
object Game {
  val minPlayers:Int = 3 // ?? As value or method(def)
  val maxPlayers:Int = 4
  val requiredVictoryPoints:Int = 10
  val defaultBankTradeFactor:Int = 4
  val unspecifiedPortFactor:Int = 3
  val specifiedPortFactor:Int = 2
}

case class Game( phase:Phase = InitPhase,
                 gameField:GameField = GameField(),
                 resourceStack:ResourceCards = Cards.getResourceCards(),
                 developmentCards:List[DevelopmentCard] = Cards.getDevStack,
                 players:Vector[Player] = Vector.empty,
                 turn:Turn = Turn( 0 ),
                 bonusCards:Map[BonusCard, Option[(Int, Int)]] = Cards.bonusCards.map( (_, Option.empty) ).toMap,
                 winner:Option[Int] = Option.empty
               ) {

  def playerExists( playerID:Int ):Boolean = playerID < players.size

  def setPhase( phase:Phase ):Game = copy( phase = phase )

  private def getAvailableResourceCards( resources:ResourceCards, stack:ResourceCards = resourceStack ):(ResourceCards, ResourceCards) = {
    resources.red( (resources, stack), ( cards:(ResourceCards, ResourceCards), r:Resource, amount:Int ) => {
      val available = cards._2.getOrElse( r, 0 )
      if ( available <= 0 )
        (cards._1.updated( r, 0 ), cards._2)
      else if ( available >= amount )
        (cards._1, cards._2.updated( r, available - amount ))
      else
        (cards._1.updated( r, available ), cards._2.updated( r, 0 ))
    } )
  }

  def drawResourceCards( playerID:Int, r:Resource, amount:Int = 1 ):Game =
    drawResourceCards( playerID, ResourceCards.of( r, amount ) )

  def drawResourceCards( playerID:Int, cards:ResourceCards ):Game = {
    val (available, newStack) = getAvailableResourceCards( cards )
    if ( available.amount > 0 ) {
      val newPlayer = players( playerID ).addResourceCards( available )
      copy(
        players = players.updated( newPlayer.id, newPlayer ),
        resourceStack = newStack
      )
    } else
      this
  }

  def dropResourceCards( playerID:Int, r:Resource, amount:Int = 1 ):Try[Game] =
    dropResourceCards( playerID, ResourceCards.of( r, amount ) )

  def dropResourceCards( playerID:Int, cards:ResourceCards ):Try[Game] = {
    players( playerID ).removeResourceCards( cards ) match {
      case Failure( e ) => Failure( e )
      case Success( nPlayer ) => Success( copy(
        players = players.updated( playerID, nPlayer ),
        resourceStack = resourceStack.add( cards )
      ) )
    }
  }

  def drawDevCard( playerID:Int ):Try[Game] = {
    dropResourceCards( playerID, Cards.developmentCardCost ) match {
      case Success( newGame ) =>
        val newPlayer = newGame.players( playerID ).addDevCard( developmentCards.head )
        Success( copy(
          players = newGame.players.updated( playerID, newPlayer ),
          turn = turn.addDrawnDevCard( developmentCards.head ),
          developmentCards = developmentCards.tail
        ) )
      case Failure( e ) => Failure( e )
    }
  }

  def updateGameField( newField:GameField ):Game = {
    copy( gameField = newField )
  }

  def updatePlayer( player:Player ):Game = copy( players = players.updated( player.id, player ) )

  def settlementAmount( playerID:Int ):Int = gameField.vertices.count( d => d._2.building.isDefined && d._2.building.get.owner == playerID )

  def roadAmount( playerID:Int ):Int = gameField.edges.count( d => d._2.road.isDefined && d._2.road.get.owner == playerID )

  def getBuildableRoadSpotsForSettlement( vID:Int ):List[Int] = {
    val vertex = gameField.findVertex( vID )
    if ( vertex.isDefined && vertex.get.building.isDefined )
      gameField.adjacentEdges( vertex.get ).filter( _.road.isEmpty ).map( _.id )
    else List.empty
  }

  def gathererPhase( number:Number, dices:(Int, Int) ):Game = {
    val empty:Map[Int, ResourceCards] = Map()
    val playerResources = gameField.hexagons.red( empty, ( resources:Map[Int, ResourceCards], row:Row[Hex] ) => {
      row.red( resources, ( resources:Map[Int, ResourceCards], hex:Option[Hex] ) => {
        if ( hex.isDefined && hex.get != gameField.robber ) hex.get.area match {
          case r:ResourceArea if r.number == number => gameField.adjacentVertices( hex.get ).red( resources, ( resources:Map[Int, ResourceCards], v:Vertex ) => {
            v.building match {
              case Some( v:Settlement ) => resources.updated( v.owner, resources.getOrElse( v.owner, Cards.getResourceCards( 0 ) ).add( r.resource, 1 ) )
              case Some( c:City ) => resources.updated( c.owner, resources.getOrElse( c.owner, Cards.getResourceCards( 0 ) ).add( r.resource, 2 ) )
              case _ => resources
            }
          } )
          case _ => resources
        }
        else resources
      } )
    } )
    val (availablePlayerResources, newStack) = playerResources.red( (playerResources, resourceStack), ( data:(Map[Int, ResourceCards], ResourceCards), playerID:Int, cards:ResourceCards ) => {
      val (available, newStack) = getAvailableResourceCards( cards, data._2 )
      if ( available.amount > 0 )
        (data._1.updated( playerID, available ), newStack)
      else
        (data._1 - playerID, data._2)
    } )
    val newPlayers = playerResources.red( players, ( pl:Vector[Player], playerID:Int, cards:ResourceCards ) => {
      pl.updated( playerID, players( playerID ).addResourceCards( cards ) )
    } )
    copy(
      players = newPlayers,
      resourceStack = newStack,
      phase = GatherPhase( dices, availablePlayerResources )
    )
  }

  def getBuildableIDsForPlayer( playerID:Int, placement:Placement, any:Boolean = false ):List[Int] = placement match {
    case Road => gameField.edges.values.red( (List.empty, List.empty), ( d:(List[Int], List[Int]), edge:Edge ) => {
      if ( !d._2.contains( edge.id ) && edge.road.isDefined && edge.road.get.owner == playerID ) {
        val nd = gameField.adjacentEdges( edge ).filter( e => !d._2.contains( e.id ) ).red( d, ( d:(List[Int], List[Int]), e:Edge ) => {
          if ( e.road.isEmpty )
            (d._1 :+ e.id, d._2 :+ e.id)
          else if ( e.road.get.owner != playerID )
            (d._1, d._2 :+ e.id)
          else d
        } )
        (nd._1, nd._2 :+ edge.id)
      } else d
    } )._1
    case Settlement if any => getAnySettlementSpotsForPlayer( playerID )
    case Settlement =>
      gameField.edges.values.red( (List.empty, List.empty), ( d:(List[Int], List[Int]), e:Edge ) => {
        if ( e.road.isDefined && e.road.get.owner == playerID )
          gameField.adjacentVertices( e ).filter( v => !d._2.contains( v.id ) ).red( d, ( d:(List[Int], List[Int]), v:Vertex ) => {
            if ( v.building.isEmpty && noBuildingInRange( v ) )
              (d._1 :+ v.id, d._2 :+ v.id)
            else
              (d._1, d._2 :+ v.id)
          } )
        else d
      } )._1
    case City => gameField.vertices.values.red( List.empty, ( l:List[Int], v:Vertex ) => v.building match {
      case Some( b:Settlement ) if b.owner == playerID => l :+ v.id
      case _ => l
    } )
    case Robber => gameField.hexagons.red( List.empty, ( l:List[Int], row:Row[Hex] ) => row.red( l, ( l:List[Int], hex:Option[Hex] ) => {
      if ( hex.isDefined && hex.get != gameField.robber && hex.get.area.isInstanceOf[LandArea] )
        l :+ hex.get.id
      else l
    } ) )
    case _ => List.empty
  }

  def getAnySettlementSpotsForPlayer( playerID:Int ):List[Int] = {
    gameField.vertices.values.red( List.empty, ( l:List[Int], v:Vertex ) => {
      if ( v.building.isEmpty && noBuildingInRange( v ) )
        l :+ v.id
      else l
    } )
  }

  def noBuildingInRange( v:Vertex ):Boolean = {
    gameField.adjacentEdges( v ).foreach( e1 => {
      gameField.adjacentVertices( e1 ).filter( _ != v ).foreach( v1 => {
        if ( v1.building.nonEmpty )
          return false
      } )
    } )
    true
  }

  private def playerHasAdjacentEdge( playerID:Int, edges:List[Edge] ):Boolean = {
    edges.foreach( e => {
      if ( e.road.isDefined && e.road.get.owner == playerID )
        return true
    } )
    false
  }

  private def playerHasAdjacentVertex( playerID:Int, vertices:List[Vertex] ):Boolean = {
    vertices.foreach( v => if ( v.building.isDefined && v.building.get.owner == playerID )
      return true
    )
    false
  }

  def roadBuildable( edge:Edge, playerID:Int ):Boolean = playerHasAdjacentEdge( playerID, gameField.adjacentEdges( edge ) ) ||
    playerHasAdjacentVertex( playerID, gameField.adjacentVertices( edge ) )

  private def roadLength( playerID:Int, e:Edge, count:Int = 0 ):Int = {
    if ( e.road.isEmpty || e.road.get.owner != playerID )
      return count
    gameField.adjacentEdges( e ).map( e2 => roadLength( playerID, e2, count + 1 ) ).max
  }

  def build( playerID:Int, structure:StructurePlacement, id:Int, anywhere:Boolean = false ):Try[Game] = {
    players( playerID ).getStructure( structure ) match {
      case Failure( e ) => Failure( e )
      case Success( newPlayer ) => structure match {
        case Road => val edge = gameField.findEdge( id )
          if ( edge.isEmpty )
            return Failure( NonExistentPlacementPoint )
          if ( edge.get.road.isDefined )
            return Failure( PlacementPointNotEmpty )
          if ( !roadBuildable( edge.get, playerID ) )
            return Failure( NoAdjacentStructure )
          val length = roadLength( playerID, edge.get )
          val newLongestRoad = length >= LongestRoadCard.minimumRoads &&
            ( bonusCards( LongestRoadCard ).isEmpty || length > bonusCards( LongestRoadCard ).get._2 )
          val newBonusCards = if ( newLongestRoad )
            bonusCards.updated( LongestRoadCard, Some( playerID, length ) )
          else bonusCards
          Success( copy(
            gameField = gameField.update( edge.get.setRoad( Road( playerID ) ) ),
            players = players.updated( playerID, newPlayer ),
            bonusCards = newBonusCards
          ) )
        case Settlement => val vertex = gameField.findVertex( id )
          if ( vertex.isEmpty )
            return Failure( NonExistentPlacementPoint )
          if ( vertex.get.building.isDefined )
            return Failure( PlacementPointNotEmpty )
          if ( !anywhere && !playerHasAdjacentEdge( playerID, gameField.adjacentEdges( vertex.get ) ) )
            return Failure( NoConnectedStructures )
          if ( !noBuildingInRange( vertex.get ) )
            return Failure( TooCloseToSettlement )
          Success( copy(
            gameField = gameField.update( vertex.get.setBuilding( Settlement( playerID ) ) ),
            players = players.updated( playerID, newPlayer.addVictoryPoint() )
          ) )
        case City => val vertex = gameField.findVertex( id )
          if ( vertex.isEmpty )
            return Failure( NonExistentPlacementPoint )
          if ( vertex.get.building.isEmpty || !vertex.get.building.get.isInstanceOf[Settlement] )
            return Failure( SettlementRequired )
          if ( vertex.get.building.get.owner != playerID )
            return Failure( InvalidPlacementPoint )
          Success( copy(
            gameField = gameField.update( vertex.get.setBuilding( City( playerID ) ) ),
            players = players.updated( playerID, newPlayer.addVictoryPoint() )
          ) )
      }
    }
  }

  def adjacentPlayers( h:Hex ):List[Int] = {
    gameField.adjacentVertices( h ).red( List.empty, ( l:List[Int], v:Vertex ) => {
      if ( v.building.isDefined && v.building.get.owner != turn.playerID && !l.contains( v.building.get.owner ) )
        l :+ v.building.get.owner
      else l
    } )
  }

  def placeRobber( hID:Int, nextPhase:Phase ):Try[(Option[Option[Resource]], Game)] = {
    val hex = gameField.findHex( hID )
    if ( hex.isEmpty )
      return Failure( NonExistentPlacementPoint )
    if ( hex.get == gameField.robber )
      return Failure( PlacementPointNotEmpty )
    if ( !hex.get.area.isInstanceOf[LandArea] )
      return Failure( RobberOnlyOnLand )
    val newGameField = gameField.copy( robber = hex.get )
    adjacentPlayers( hex.get ) match {
      case Nil => Success( Option.empty, copy(
        gameField = newGameField,
        phase = nextPhase
      ) )
      case List( stealPlayerID ) =>
        robberSteal( turn.playerID, stealPlayerID, nextPhase, newGameField ).map( d => (Some( d._1 ), d._2) )
      case _ => Success( Option.empty, copy(
        gameField = newGameField,
        phase = RobberStealPhase( nextPhase )
      ) )
    }
  }

  def robberSteal( playerID:Int, stealPlayerID:Int, nextPhase:Phase, newGameField:GameField = gameField ):Try[(Option[Resource], Game)] = {
    if ( !playerHasAdjacentVertex( stealPlayerID, newGameField.adjacentVertices( newGameField.robber ) ) )
      return Failure( NoAdjacentStructure )
    val resource = players( stealPlayerID ).randomHandResource()
    if ( resource.isDefined ) {
      val stealPlayer = players( stealPlayerID ).removeResourceCard( resource.get ).get
      val newPlayer = players( playerID ).addResourceCard( resource.get )
      Success( resource, copy(
        players = players.updated( playerID, newPlayer ).updated( stealPlayerID, stealPlayer ),
        gameField = newGameField,
        phase = nextPhase
      ) )
    } else
      Success( resource, copy(
        gameField = newGameField,
        phase = nextPhase
      ) )
  }

  def getBankTradeFactor( playerID:Int, r:Resource ):Int = {
    gameField.vertices.values.red( Game.defaultBankTradeFactor, ( factor:Int, v:Vertex ) => {
      if ( v.building.isDefined && v.building.get.owner == playerID && v.port.isDefined )
        if ( v.port.get.specific.isDefined && v.port.get.specific.get == r )
          Game.specifiedPortFactor
        else if ( v.port.get.specific.isEmpty && Game.unspecifiedPortFactor < factor )
          Game.unspecifiedPortFactor
        else factor
      else factor
    } )
  }
}