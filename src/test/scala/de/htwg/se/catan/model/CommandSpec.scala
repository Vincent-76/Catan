package de.htwg.se.catan.model

import Card._
import de.htwg.se.catan.model.commands._
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.model.impl.placement.{ CityPlacement, RoadPlacement, SettlementPlacement }
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import de.htwg.se.catan.model.impl.turn.ClassicTurnImpl
import de.htwg.se.catan.model.state._
import de.htwg.se.catan.util._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.TreeMap
import scala.util.{ Failure, Success, Try }

class CommandSpec extends AnyWordSpec with Matchers {
  "Command" when {
    val newGame:ClassicGameImpl = new ClassicGameImpl( ClassicGameFieldImpl( 1 ), ClassicTurnImpl(), 1, ( pID:PlayerID, color:PlayerColor, name:String ) => ClassicPlayerImpl( pID, color, name ), "ClassicPlayerImpl" )
    "AbortPlayerTradeCommand" should {
      val state = PlayerTradeEndState( ResourceCards.of(), ResourceCards.of(), Map.empty )
      "success" in {
        val command = AbortPlayerTradeCommand( state )
        val res = command.doStep( newGame )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [ActionState]
        command.undoStep( res.get._1 ).state shouldBe state
      }
    }
    "AddPlayerCommand" should {
      val state = InitPlayerState()
      val game = newGame.setState( state )
      "fail because player name is empty" in {
        AddPlayerCommand( Green, "", state ).doStep( game ) shouldBe
          Failure( PlayerNameEmpty )
      }
      "fail because player name is too long" in {
        val tooLongName = "".toLength( game.maxPlayerNameLength + 1 )
        AddPlayerCommand( Green, tooLongName, state ).doStep( game ) shouldBe
          Failure( PlayerNameTooLong( tooLongName ) )
      }
      val name = "A"
      val game2 = game.addPlayer( Green, name )
      val command = AddPlayerCommand( Green, name, state )
      "fail because player name already exists" in {
        command.doStep( game2 ) shouldBe Failure( PlayerNameAlreadyExists( name ) )
      }
      "fail because player color is already in use" in {
        AddPlayerCommand( Green, "B", state ).doStep( game2 ) shouldBe Failure( PlayerColorIsAlreadyInUse( Green ) )
      }
      "success" in {
        val res = command.doStep( game )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.players should have size 1
        res.get._1.players.head._2.name shouldBe name
        res.get._1.players.head._2.color shouldBe Green
        val undoRed = command.undoStep( res.get._1 )
        undoRed.state shouldBe state
        undoRed.players shouldBe empty
      }
      "success with set to InitBeginnerState" in {
        val game3 = game.copy( playersVal = TreeMap( ( 0 until game.maxPlayers - 1 ).map( i => {
          val pID = new PlayerID( i )
          (pID, ClassicPlayerImpl( pID, Blue, i.toString ))
        } ).toArray:_* )( PlayerOrdering ) )
        val res = command.doStep( game3 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.players should have size game.maxPlayers
        res.get._1.players.last._2.name shouldBe name
        res.get._1.players.last._2.color shouldBe Green
        val undoRed = command.undoStep( res.get._1 )
        undoRed.state shouldBe state
        undoRed.players should have size game.maxPlayers - 1
      }
    }
    "BankTradeCommand" should {
      val playerResources = ResourceCards.of( wood = 5, clay = 3, sheep = 8, wheat = 9 )
      val pID = new PlayerID( 0 )
      val game = newGame.copy(
        stateVal = ActionState(),
        playersVal = newGame.playersVal + ( pID -> ClassicPlayerImpl( pID, Green, "A", resourcesVal = playerResources ) ),
        turnVal = ClassicTurnImpl( pID )
      )
      "fail because of insufficient resources" in {
        BankTradeCommand(
          ResourceCards.of( wood = 6 ),
          ResourceCards.of( clay = 1 )
        ).doStep( game ) shouldBe Failure( InsufficientResources )
        BankTradeCommand(
          ResourceCards.of( wood = 1 ),
          ResourceCards.of( clay = 1 )
        ).doStep( game ) shouldBe Failure( InsufficientResources )
      }
      "fail because of insufficient bank resources" in {
        val game2 = game.copy( resourceStack = ResourceCards.of() )
        BankTradeCommand(
          ResourceCards.of( wood = 5 ),
          ResourceCards.of( clay = 1 ),
        ).doStep( game2 ) shouldBe Failure( InsufficientBankResources )
      }
      "success" in {
        val command = BankTradeCommand(
          ResourceCards.of( clay = 1, sheep = 8, wheat = 9 ),
          ResourceCards.of( clay = 3 ),
        )
        command.undoStep( game ) shouldBe game
        val res = command.doStep( game )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[BankTradedInfo]]
        res.get._1.player( pID ).resources shouldBe
          playerResources.add( ResourceCards.of( sheep = -4, clay = 3, wheat = -8 ) )
        command.undoStep( res.get._1 ).player( pID ).resources shouldBe playerResources
      }
    }
    "BuildCommand" should {
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val game = newGame.copy(
        playersVal = newGame.playersVal + ( pID -> ClassicPlayerImpl( pID, Green, "A" ) ) + ( pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ) ),
        turnVal = ClassicTurnImpl( pID )
      )
      "fail because of insufficient structures" in {
        val p = game.player.asInstanceOf[ClassicPlayerImpl]
        val game2 = game.updatePlayer( p.copy( structures = p.structures.updated( RoadPlacement, 0 ) ) )
        BuildCommand( 0, BuildState( RoadPlacement ) ).doStep( game2 ) shouldBe
          Failure( InsufficientStructures( RoadPlacement) )
      }
      "fail road because of non existent placement point" in {
        BuildCommand( -1, BuildState( RoadPlacement ) ).doStep( game ) shouldBe
          Failure( NonExistentPlacementPoint( -1 ) )
      }
      "fail road because of placement point is not empty" in {
        val edge = game.gameField.edgeList.head
        val game2 = game.setGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) ) )
        BuildCommand( edge.id, BuildState( RoadPlacement ) ).doStep( game2 ) shouldBe
          Failure( PlacementPointNotEmpty( edge.id ) )
      }
      "fail road because of no connected structures" in {
        val edge = game.gameField.edgeList.head
        BuildCommand( edge.id, BuildState( RoadPlacement ) ).doStep( game ) shouldBe
          Failure( NoConnectedStructures( edge.id ) )
      }
      "success road" in {
        val edge = game.gameField.edgeList.head
        val edge2 = game.gameField.adjacentEdges( edge ).head
        val game2 = game.setGameField( game.gameField.update( edge2.setRoad( Some( Road( pID ) ) ) ) )
        val command = BuildCommand( edge.id, BuildState( RoadPlacement ) )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[BuiltInfo]]
        res.get._1.state shouldBe a [ActionState]
        res.get._1.player.asInstanceOf[ClassicPlayerImpl].structures( RoadPlacement ) shouldBe RoadPlacement.available - 1
        val resEdge = res.get._1.gameField.findEdge( edge.id )
        resEdge shouldNot be( None )
        resEdge.get.road shouldBe Some( Road( pID ) )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe a [BuildState]
        undoRes.player.asInstanceOf[ClassicPlayerImpl].structures( RoadPlacement ) shouldBe RoadPlacement.available
        val undoEdge = undoRes.gameField.findEdge( edge.id )
        undoEdge shouldNot be( None )
        undoEdge.get.road shouldBe None
      }
      "success road with first bonus card" in {
        val (game2, edge) = ( 1 until LongestRoadCard.minimumRoads ).red( (game, game.gameField.edgeList.head),
          ( data:(Game, Edge), _ ) => { (
            data._1.setGameField( data._1.gameField.update( data._2.setRoad( Some( Road( pID ) ) ) ) ),
            data._1.gameField.adjacentEdges( data._2 ).find( _.road.isEmpty ).get
          ) } )
        val command = BuildCommand( edge.id, BuildState( RoadPlacement ) )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._1.bonusCards( LongestRoadCard ) shouldBe Some( pID, LongestRoadCard.minimumRoads )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.bonusCards( LongestRoadCard ) shouldBe None
      }

      "success road with new bonus card" in {
        val (game2, edge) = ( 0 until LongestRoadCard.minimumRoads ).red( (game, game.gameField.edgeList.head),
          ( data:(Game, Edge), _ ) => { (
            data._1.setGameField( data._1.gameField.update( data._2.setRoad( Some( Road( pID ) ) ) ) ),
            data._1.gameField.adjacentEdges( data._2 ).find( e => e.road.isEmpty && (e.h1.isLand || e.h2.isLand) ).get
          ) } )
        val game3 = game2.setBonusCard( LongestRoadCard, Some( pID1, LongestRoadCard.minimumRoads ) )
        val command = BuildCommand( edge.id, BuildState( RoadPlacement ) )
        val res = command.doStep( game3 )
        res shouldBe a [Success[_]]
        res.get._1.bonusCards( LongestRoadCard ) shouldBe Some( pID, LongestRoadCard.minimumRoads + 1 )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.bonusCards( LongestRoadCard ) shouldBe Some( pID1, LongestRoadCard.minimumRoads )
      }
      "fail settlement because of non existent placement point" in {
        BuildCommand( -1, BuildState( SettlementPlacement ) ).doStep( game ) shouldBe
          Failure( NonExistentPlacementPoint( -1 ) )
      }
      "fail settlement because of placement point is not empty" in {
        val vertex = game.gameField.vertexList.head
        val game2 = game.setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        BuildCommand( vertex.id, BuildState( SettlementPlacement ) ).doStep( game2 ) shouldBe
          Failure( PlacementPointNotEmpty( vertex.id ) )
      }
      "fail settlement because of no connected structures" in {
        val vertex = game.gameField.vertexList.head
        BuildCommand( vertex.id, BuildState( SettlementPlacement ) ).doStep( game ) shouldBe
          Failure( NoConnectedStructures( vertex.id ) )
      }
      "fail settlement because of too close to building" in {
        val vertex = game.gameField.vertexList.head
        val edge = game.gameField.adjacentEdges( vertex ).find( e => game.gameField.adjacentVertices( e ).size > 1 )
        edge shouldNot be( None )
        val vertex2 = game.gameField.adjacentVertices( edge.get ).filter( _ != vertex ).head
        val game2 = game.setGameField( game.gameField.update( edge.get.setRoad( Some( Road( pID ) ) ) )
            .update( vertex2.setBuilding( Some( Settlement( pID ) ) ) ) )
        BuildCommand( vertex.id, BuildState( SettlementPlacement ) ).doStep( game2 ) shouldBe
          Failure( TooCloseToBuilding( vertex.id ) )
      }
      "success settlement" in {
        val vertex = game.gameField.vertexList.head
        val edge = game.gameField.adjacentEdges( vertex ).head
        val game2 = game.setGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) ) )
        val command = BuildCommand( vertex.id, BuildState( SettlementPlacement ) )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[BuiltInfo]]
        res.get._1.state shouldBe a [ActionState]
        res.get._1.player.asInstanceOf[ClassicPlayerImpl].structures( SettlementPlacement ) shouldBe SettlementPlacement.available - 1
        val resVertex = res.get._1.gameField.findVertex( vertex.id )
        resVertex shouldNot be( None )
        resVertex.get.building shouldBe Some( Settlement( pID ) )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe a [BuildState]
        undoRes.player.asInstanceOf[ClassicPlayerImpl].structures( SettlementPlacement ) shouldBe SettlementPlacement.available
        val undoVertex = undoRes.gameField.findVertex( vertex.id )
        undoVertex shouldNot be( None )
        undoVertex.get.building shouldBe None
      }
      "fail city because of non existent placement point" in {
        BuildCommand( -1, BuildState( CityPlacement ) ).doStep( game ) shouldBe
          Failure( NonExistentPlacementPoint( -1 ) )
      }
      "fail city because of settlement required" in {
        val vertex = game.gameField.vertexList.head
        BuildCommand( vertex.id, BuildState( CityPlacement ) ).doStep( game ) shouldBe
          Failure( SettlementRequired( vertex.id ) )
      }
      "fail city because of invalid placement point" in {
        val vertex = game.gameField.vertexList.head
        vertex shouldNot be( None )
        val game2 = game.setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( new PlayerID( 1 ) ) ) ) ) )
        BuildCommand( vertex.id, BuildState( CityPlacement ) ).doStep( game2 ) shouldBe
          Failure( InvalidPlacementPoint( vertex.id ) )
      }
      "success city" in {
        val vertex = game.gameField.vertexList.head
        val p = game.player.asInstanceOf[ClassicPlayerImpl]
        val game2 = game.setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
          .updatePlayer( p.copy( structures = p.structures.updated( SettlementPlacement, SettlementPlacement.available - 1 ) ) )
        val command = BuildCommand( vertex.id, BuildState( CityPlacement ) )
        val res = command.doStep( game2 )
        res shouldNot be( None )
        res.get._2 shouldBe a [Some[BuiltInfo]]
        res.get._1.state shouldBe a [ActionState]
        res.get._1.player.asInstanceOf[ClassicPlayerImpl].structures( CityPlacement ) shouldBe CityPlacement.available - 1
        res.get._1.player.asInstanceOf[ClassicPlayerImpl].structures( SettlementPlacement ) shouldBe SettlementPlacement.available
        val resVertex = res.get._1.gameField.findVertex( vertex.id )
        resVertex shouldNot be( None )
        resVertex.get.building shouldBe Some( City( pID ) )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe a [BuildState]
        undoRes.player.asInstanceOf[ClassicPlayerImpl].structures( CityPlacement ) shouldBe CityPlacement.available
        undoRes.player.asInstanceOf[ClassicPlayerImpl].structures( SettlementPlacement ) shouldBe SettlementPlacement.available - 1
        val undoVertex = undoRes.gameField.findVertex( vertex.id )
        undoVertex shouldNot be( None )
        undoVertex.get.building shouldBe Some( Settlement( pID ) )
      }
    }
    "BuildInitRoadCommand" should {
      val vertex = newGame.gameField.findVertex( 1 ).get
      val state = BuildInitRoadState( vertex.id )
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val pID2 = new PlayerID( 2 )
      val game = newGame.copy(
        stateVal = state,
        gameFieldVal = newGame.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ),
        playersVal = TreeMap(
          pID -> ClassicPlayerImpl( pID, Green, "A" ),
          pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ),
          pID2 -> ClassicPlayerImpl( pID2, Yellow, "C" ),
        )( PlayerOrdering ),
        turnVal = ClassicTurnImpl( pID )
      )
      "fail because of invalid placement point" in {
        BuildInitRoadCommand( 16, state ).doStep( game ) shouldBe
          Failure( InvalidPlacementPoint( 16 ) )
      }
      "fail because of build failure" in {
        val p = game.player.asInstanceOf[ClassicPlayerImpl]
        val game2 = game.updatePlayer( p.copy( structures = p.structures.updated( RoadPlacement, 0 ) ) )
        BuildInitRoadCommand( 3, state ).doStep( game2 ) shouldBe a [Failure[_]]
      }
      "success with next turn" in {
        val command = BuildInitRoadCommand( 3, state )
        val res = command.doStep( game )
        res shouldNot be( None )
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [BuildInitSettlementState]
        res.get._1.turn.playerID shouldBe pID1
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        val undoEdge = undoRes.gameField.findEdge( 3 )
        undoEdge shouldNot be( None )
        undoEdge.get.road shouldBe None
        undoRes.turn.playerID shouldBe pID
        undoRes.player.asInstanceOf[ClassicPlayerImpl].structures( RoadPlacement ) shouldBe RoadPlacement.available
      }
      "success with same turn" in {
        val vertex1 = game.gameField.findVertex( 2 )
        vertex1 shouldNot be( None )
        val vertex2 = game.gameField.findVertex( 5 )
        vertex2 shouldNot be( None )
        val state2 = BuildInitRoadState( 5 )
        val game2 = game.copy(
          turnVal = ClassicTurnImpl( pID2 ),
          stateVal = state2,
          gameFieldVal = game.gameField.update( vertex1.get.setBuilding( Some( Settlement( pID1 ) ) ) )
            .update( vertex2.get.setBuilding( Some( Settlement( pID2 ) ) ) )
        )
        val command = BuildInitRoadCommand( 9, state2 )
        val res = command.doStep( game2 )
        res shouldNot be( None )
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [BuildInitSettlementState]
        res.get._1.turn.playerID shouldBe pID2
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state2
        undoRes.turn.playerID shouldBe pID2
        undoRes.player.asInstanceOf[ClassicPlayerImpl].structures( RoadPlacement ) shouldBe RoadPlacement.available
      }
      "success with previous turn" in {
        val vertex1 = game.gameField.findVertex( 2 )
        vertex1 shouldNot be( None )
        val vertex2 = game.gameField.findVertex( 5 )
        vertex2 shouldNot be( None )
        val vertex3 = game.gameField.findVertex( 13 )
        vertex3 shouldNot be( None )
        val state2 = BuildInitRoadState( 13 )
        val game2 = game.copy(
          turnVal = ClassicTurnImpl( pID2 ),
          stateVal = state2,
          gameFieldVal = game.gameField.update( vertex1.get.setBuilding( Some( Settlement( pID1 ) ) ) )
            .update( vertex2.get.setBuilding( Some( Settlement( pID2 ) ) ) )
            .update( vertex3.get.setBuilding( Some( Settlement( pID2 ) ) ) )
        )
        val command = BuildInitRoadCommand( 36, state2 )
        val res = command.doStep( game2 )
        res shouldNot be( None )
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [BuildInitSettlementState]
        res.get._1.turn.playerID shouldBe pID1
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state2
        undoRes.turn.playerID shouldBe pID2
        undoRes.player.asInstanceOf[ClassicPlayerImpl].structures( RoadPlacement ) shouldBe RoadPlacement.available
      }
      "success with continue" in {
        val vertex1 = game.gameField.findVertex( 2 )
        vertex1 shouldNot be( None )
        val vertex2 = game.gameField.findVertex( 5 )
        vertex2 shouldNot be( None )
        val vertex3 = game.gameField.findVertex( 13 )
        vertex3 shouldNot be( None )
        val vertex4 = game.gameField.findVertex( 11 )
        vertex4 shouldNot be( None )
        val vertex5 = game.gameField.findVertex( 9 )
        vertex5 shouldNot be( None )
        val state2 = BuildInitRoadState( 9 )
        val game2 = game.copy(
          turnVal = ClassicTurnImpl( pID ),
          stateVal = state2,
          gameFieldVal = game.gameField.update( vertex1.get.setBuilding( Some( Settlement( pID1 ) ) ) )
            .update( vertex2.get.setBuilding( Some( Settlement( pID2 ) ) ) )
            .update( vertex3.get.setBuilding( Some( Settlement( pID2 ) ) ) )
            .update( vertex4.get.setBuilding( Some( Settlement( pID1 ) ) ) )
            .update( vertex5.get.setBuilding( Some( Settlement( pID ) ) ) )
        )
        val command = BuildInitRoadCommand( 30, state2 )
        val res = command.doStep( game2 )
        res shouldNot be( None )
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [NextPlayerState]
        res.get._1.turn.playerID shouldBe pID
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state2
        undoRes.turn.playerID shouldBe pID
        undoRes.player.asInstanceOf[ClassicPlayerImpl].structures( RoadPlacement ) shouldBe RoadPlacement.available
      }
    }
    "BuildInitSettlementCommand" should {
      val state = BuildInitSettlementState()
      val pID = new PlayerID( 0 )
      val game = newGame.copy(
        stateVal = state,
        turnVal = ClassicTurnImpl( pID ),
        playersVal = newGame.playersVal + ( pID -> ClassicPlayerImpl( pID, Green, "A" ) )
      )
      "fail because of build failure" in {
        val vertex = game.gameField.vertexList.head
        val game2 = game.setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        BuildInitSettlementCommand( vertex.id, state ).doStep( game2 ) shouldBe a [Failure[_]]
      }
      "success without resource cards" in {
        val vertex = game.gameField.vertexList.head
        val command = BuildInitSettlementCommand( vertex.id, state )
        val res = command.doStep( game )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe BuildInitRoadState( vertex.id )
        res.get._1.player.asInstanceOf[ClassicPlayerImpl].structures( SettlementPlacement ) shouldBe SettlementPlacement.available - 1
        val resVertex = res.get._1.gameField.findVertex( vertex.id )
        resVertex shouldNot be( None )
        resVertex.get.building shouldBe Some( Settlement( pID ) )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player.asInstanceOf[ClassicPlayerImpl].structures( RoadPlacement ) shouldBe RoadPlacement.available
        val undoVertex = undoRes.gameField.findVertex( vertex.id )
        undoVertex shouldNot be( None )
        undoVertex.get.building shouldBe None
      }
      "success with resource cards" in {
        val vertex = game.gameField.findVertex( 1 )
        vertex shouldNot be( None )
        val vertex2 = game.gameField.findVertex( 3 )
        vertex2 shouldNot be( None )
        val game2 = game.setGameField( game.gameField.update( vertex2.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        val command = BuildInitSettlementCommand( vertex.get.id, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[GotResourcesInfo]]
        res.get._1.state shouldBe BuildInitRoadState( vertex.get.id )
        res.get._1.player.resources.filter( _._2 > 0 ) shouldBe List( vertex.get.h1, vertex.get.h2, vertex.get.h3 )
          .filter( _.area.f.isInstanceOf[Resource] )
          .map( h => (h.area.f.asInstanceOf[Resource], 1) ).toMap
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
    }
    "BuyDevCardCommand" should {
      val pID = new PlayerID( 0 )
      val state = ActionState()
      val game = newGame.copy(
        stateVal = state,
        turnVal = ClassicTurnImpl( pID ),
        playersVal = newGame.playersVal + ( pID -> ClassicPlayerImpl( pID, Green, "A" ) )
      )
      "fail" in {
        BuyDevCardCommand( state ).doStep( game ) shouldBe a [Failure[_]]
      }
      "success" in {
        val game2 = game.updatePlayer( game.player.addResourceCards( DevelopmentCard.cardCost ) )
        val command = BuyDevCardCommand( state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[DrawnDevCardInfo]]
        res.get._1.player.devCards should have size 1
        res.get._1.player.resources shouldBe ResourceCards.of()
        res.get._1.turn.drawnDevCards should have size 1
        res.get._1.asInstanceOf[ClassicGameImpl].developmentCards should have size game2.developmentCards.size - 1
        val undoRes = command.undoStep( res.get._1 ).asInstanceOf[ClassicGameImpl]
        undoRes.developmentCards shouldBe game2.developmentCards
        undoRes.player.devCards shouldBe empty
        undoRes.turn.drawnDevCards shouldBe empty
        undoRes.player.resources.filter( _._2 > 0 ) shouldBe DevelopmentCard.cardCost
      }
    }
    "ChangeStateCommand" should {
      "success" in {
        val state = ActionState()
        val nextState = NextPlayerState()
        val command = ChangeStateCommand( state, nextState, None )
        val res = command.doStep( newGame )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe nextState
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
    }
    "DevBuildRoadCommand" should {
      val nextState = ActionState()
      val state = DevRoadBuildingState( nextState )
      val pID = new PlayerID( 0 )
      val game = newGame.copy(
        stateVal = state,
        turnVal = ClassicTurnImpl( pID ),
        playersVal = newGame.playersVal + ( pID -> ClassicPlayerImpl( pID, Green, "A" ) )
      )
      "fail because of build failure" in {
        val edge = game.gameField.edgeList.head
        DevBuildRoadCommand( edge.id, state ).doStep( game ) shouldBe a [Failure[_]]
      }
      "success with first road" in {
        val vertex = game.gameField.vertexList.head
        val edge = game.gameField.adjacentEdges( vertex ).filter( e => e.h1.isLand || e.h2.isLand ).head
        val game2 = game.setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        val command = DevBuildRoadCommand( edge.id, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[BuiltInfo]]
        res.get._1.state shouldBe DevRoadBuildingState( nextState, 1 )
        res.get._1.player.asInstanceOf[ClassicPlayerImpl].structures( RoadPlacement ) shouldBe RoadPlacement.available - 1
        val resEdge = res.get._1.gameField.findEdge( edge.id )
        resEdge shouldNot be( None )
        resEdge.get.road shouldBe Some( Road( pID ) )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player.asInstanceOf[ClassicPlayerImpl].structures( RoadPlacement ) shouldBe RoadPlacement.available
        val undoEdge = undoRes.gameField.findEdge( edge.id )
        undoEdge shouldNot be( None )
        undoEdge.get.road shouldBe None
      }
      "success with second road" in {
        val vertex = game.gameField.vertexList.head
        val edge = game.gameField.adjacentEdges( vertex ).filter( e => e.h1.isLand || e.h2.isLand ).head
        val state2 = DevRoadBuildingState( nextState, 1 )
        val game2 = game.setState( state2 )
          .setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        val command = DevBuildRoadCommand( edge.id, state2 )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[BuiltInfo]]
        res.get._1.state shouldBe nextState
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state2
      }
      "success with insufficient structures info" in {
        val vertex = game.gameField.vertexList.head
        val edge = game.gameField.adjacentEdges( vertex ).filter( e => e.h1.isLand || e.h2.isLand ).head
        val p = game.player.asInstanceOf[ClassicPlayerImpl]
        val game2 = game.updatePlayer( p.copy( structures = p.structures.updated( RoadPlacement, 1 ) ) )
          .setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        val command = DevBuildRoadCommand( edge.id, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[InsufficientStructuresInfo]]
        res.get._1.state shouldBe nextState
        res.get._1.player.asInstanceOf[ClassicPlayerImpl].structures( RoadPlacement ) shouldBe 0
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player.asInstanceOf[ClassicPlayerImpl].structures( RoadPlacement ) shouldBe 1
      }
      "success with no placement points info" in {
        val edge = game.gameField.findEdge( 1 )
        val vertex = game.gameField.adjacentVertices( edge.get ).head
        val pID1 = new PlayerID( 1 )
        val game2 = game.setGameField( game.gameField.adjacentEdges( edge.get ).red( game.gameField, ( g:GameField, e:Edge ) =>
          g.update( e.setRoad( Some( Road( pID1 ) ) ) )
        ).update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        val command = DevBuildRoadCommand( edge.get.id, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[NoPlacementPointsInfo]]
        res.get._1.state shouldBe nextState
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
    }
    "DiceOutBeginnerCommand" should {
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val pID2 = new PlayerID( 2 )
      val game = newGame.copy(
        playersVal = TreeMap(
          pID -> ClassicPlayerImpl( pID, Green, "A" ),
          pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ),
          pID2 -> ClassicPlayerImpl( pID2, Yellow, "C" ),
        )( PlayerOrdering )
      )
      "fail because of unique beginner exists" in {
        val state = InitBeginnerState( Some( pID ) )
        val game2 = game.setState( state )
        DiceOutBeginnerCommand( state ).doStep( game2 ) shouldBe
          Failure( UniqueBeginnerExists )
      }
      "success with tie" in {
        val state = InitBeginnerState()
        val game2 = game.setState( state )
        val command = DiceOutBeginnerCommand( state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [InitBeginnerState]
        res.get._1.state.asInstanceOf[InitBeginnerState].beginner shouldBe None
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
      "success without tie" in {
        val state = InitBeginnerState( diceValues = Map( pID -> 2, pID1 -> 2, pID2 -> 1 ), counter = 2 )
        val game2 = game.setState( state )
        val command = DiceOutBeginnerCommand( state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [InitBeginnerState]
        res.get._1.state.asInstanceOf[InitBeginnerState].beginner shouldBe Some( pID1 )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
    }
    "DropHandCardsCommand" should {
      val pID = new PlayerID( 0 )
      val game = newGame.copy(
        playersVal = newGame.playersVal + ( pID -> ClassicPlayerImpl( pID, Green, "A" ).addResourceCards( ResourceCards.of( wood = newGame.maxHandCards + 1 ) ) ),
        turnVal = ClassicTurnImpl( pID )
      )
      "fail because of invalid resource amount" in {
        val state = DropHandCardsState( pID )
        val game2 = game.setState( state )
        val dropResources = ResourceCards.of( wood = game.maxHandCards + 10 )
        DropHandCardsCommand( state, dropResources ).doStep( game2 ) shouldBe
          Failure( InvalidResourceAmount( dropResources.amount ) )
      }
      "success with next" in {
        val state = DropHandCardsState( pID )
        val pID1 = new PlayerID( 1 )
        val game2 = game.copy(
          stateVal = state,
          playersVal = game.playersVal + ( pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ).addResourceCards( ResourceCards.of( clay = game.maxHandCards + 1 ) ) )
        )
        val dropResources = ResourceCards.of( wood = game.player.resources( Wood ) / 2 )
        val command = DropHandCardsCommand( state, dropResources )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[LostResourcesInfo]]
        res.get._1.state shouldBe DropHandCardsState( pID1, List( pID ) )
        Success( res.get._1.player.resources ) shouldBe game2.player.resources.subtract( dropResources )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player.resources shouldBe game2.player.resources
      }
      "success without next" in {
        val state = DropHandCardsState( pID )
        val game2 = game.setState( state )
        val dropResources = ResourceCards.of( wood = game.player.resources( Wood ) / 2 )
        val command = DropHandCardsCommand( state, dropResources )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[LostResourcesInfo]]
        res.get._1.state shouldBe a [RobberPlaceState]
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
    }
    "EndTurnCommand" should {
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val game = newGame.copy(
        playersVal = TreeMap(
          pID -> ClassicPlayerImpl( pID, Green, "A" ),
          pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ),
        )( PlayerOrdering ),
        turnVal = ClassicTurnImpl( pID )
      )
      "success" in {
        val state = ActionState()
        val game2 = game.setState( state )
        val command = EndTurnCommand( state )
        val undoRes1 = command.undoStep( game2 )
        undoRes1.turn shouldBe ClassicTurnImpl( pID1 )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [NextPlayerState]
        res.get._1.turn shouldBe ClassicTurnImpl( pID1 )
        res.get._1.round shouldBe game2.round + 1
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.turn shouldBe ClassicTurnImpl( pID )
        undoRes.round shouldBe game2.round
      }
    }
    "MonopolyCommand" should {
      val nextState = ActionState()
      val state = MonopolyState( nextState )
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val pID2 = new PlayerID( 2 )
      val game = newGame.copy(
        stateVal = state,
        playersVal = TreeMap(
          pID -> ClassicPlayerImpl( pID, Green, "A" ),
          pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ),
          pID2 -> ClassicPlayerImpl( pID2, Yellow, "C" ).addResourceCards( ResourceCards.of( wood = 3 ) ),
        )( PlayerOrdering ),
        turnVal = ClassicTurnImpl( pID )
      )
      "success" in {
        val command = MonopolyCommand( Wood, state )
        val undoRes1 = command.undoStep( game )
        undoRes1.state shouldBe state
        val res = command.doStep( game )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[ResourceChangeInfo]]
        res.get._1.state shouldBe nextState
        res.get._1.player.resources shouldBe ResourceCards.of( wood = 3 )
        res.get._1.player( pID2 ).resources shouldBe ResourceCards.of()
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player.resources shouldBe ResourceCards.of()
        undoRes.player( pID2 ).resources shouldBe ResourceCards.of( wood = 3 )
      }
    }
    "PlaceRobberCommand" should {
      val nextState = ActionState()
      val state = RobberPlaceState( nextState )
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val pID2 = new PlayerID( 2 )
      val game = newGame.copy(
        stateVal = state,
        playersVal = TreeMap(
          pID -> ClassicPlayerImpl( pID, Green, "A" ),
          pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ).addResourceCard( Wood ),
          pID2 -> ClassicPlayerImpl( pID2, Yellow, "C" ),
        )( PlayerOrdering ),
        turnVal = ClassicTurnImpl( pID )
      )
      "fail because of non existent placement point" in {
        PlaceRobberCommand( -1, state ).doStep( game ) shouldBe
          Failure( NonExistentPlacementPoint( -1 ) )
      }
      "fail because of placement point is not empty" in {
        PlaceRobberCommand( game.gameField.robberHex.id, state ).doStep( game ) shouldBe
          Failure( PlacementPointNotEmpty( game.gameField.robberHex.id ) )
      }
      "fail because of robber is placed in water" in {
        PlaceRobberCommand( 1, state ).doStep( game ) shouldBe
          Failure( RobberOnlyOnLand )
      }
      "success without stealing" in {
        val command = PlaceRobberCommand( 19, state )
        val undoRes1 = command.undoStep( game )
        undoRes1.state shouldBe state
        undoRes1.gameField shouldBe game.gameField
        val res = command.doStep( game )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe nextState
        res.get._1.gameField.robberHex.id shouldBe 19
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.gameField.robberHex.id shouldBe game.gameField.robberHex.id
      }
      "success with stealing from one with resource" in {
        val hex = game.gameField.findHex( 19 )
        val vertex = game.gameField.adjacentVertices( hex.get ).head
        val game2 = game.setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID1 ) ) ) ) )
        val command = PlaceRobberCommand( 19, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[ResourceChangeInfo]]
        res.get._1.state shouldBe nextState
        res.get._1.player.resources shouldBe ResourceCards.of( wood = 1 )
        res.get._1.player( pID1 ).resources shouldBe ResourceCards.of()
        res.get._1.gameField.robberHex.id shouldBe 19
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.gameField.robberHex.id shouldBe game.gameField.robberHex.id
        undoRes.player.resources shouldBe ResourceCards.of()
        undoRes.player( pID1 ).resources shouldBe ResourceCards.of( wood = 1 )
      }
      "success with stealing from multiple" in {
        val hex = game.gameField.findHex( 19 )
        val vertices = game.gameField.adjacentVertices( hex.get )
        val vertex1 = vertices.head
        val vertex2 = vertices( 1 )
        val game2 = game.setGameField( game.gameField.update( vertex1.setBuilding( Some( Settlement( pID1 ) ) ) )
          .update( vertex2.setBuilding( Some( Settlement( pID2 ) ) ) ) )
        val command = PlaceRobberCommand( 19, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe RobberStealState( List( pID1, pID2 ), nextState )
        res.get._1.gameField.robberHex.id shouldBe 19
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.gameField.robberHex.id shouldBe game.gameField.robberHex.id
      }
    }
    "PlayerTradeCommand" should {
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val pID2 = new PlayerID( 2 )
      val game = newGame.copy(
        playersVal = TreeMap(
          pID -> ClassicPlayerImpl( pID, Green, "A" ).addResourceCard( Wood ),
          pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ),
          pID2 -> ClassicPlayerImpl( pID2, Yellow, "C" ).addResourceCard( Clay ),
        )( PlayerOrdering ),
        turnVal = ClassicTurnImpl( pID )
      )
      "fail because of invalid player specified" in {
        val state = PlayerTradeEndState( ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 1 ), Map( pID1 -> false ) )
        val game2 = game.setState( state )
        PlayerTradeCommand( pID2, state ).doStep( game2 ) shouldBe
          Failure( InvalidPlayer( pID2 ) )
      }
      "fail because of insufficient resources" in {
        val state = PlayerTradeEndState( ResourceCards.of( wood = 2 ), ResourceCards.of( clay = 1 ), Map( pID2 -> true ) )
        val game2 = game.setState( state )
        PlayerTradeCommand( pID2, state ).doStep( game2 ) shouldBe
          Failure( InsufficientResources )
      }
      "fail because of trade player has insufficient resources" in {
        val state = PlayerTradeEndState( ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 2 ), Map( pID2 -> true ) )
        val game2 = game.setState( state )
        PlayerTradeCommand( pID2, state ).doStep( game2 ) shouldBe
          Failure( TradePlayerInsufficientResources )
      }
      "success" in {
        val state = PlayerTradeEndState( ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 1 ), Map( pID2 -> true ) )
        val game2 = game.setState( state )
        val command = PlayerTradeCommand( pID2, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[ResourceChangeInfo]]
        res.get._1.state shouldBe ActionState()
        res.get._1.player.resources shouldBe ResourceCards.of( clay = 1 )
        res.get._1.player( pID2 ).resources shouldBe ResourceCards.of( wood = 1 )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player.resources shouldBe ResourceCards.of( wood = 1 )
        undoRes.player( pID2 ).resources shouldBe ResourceCards.of( clay = 1 )
      }
    }
    "PlayerTradeDecisionCommand" should {
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val pID2 = new PlayerID( 2 )
      val game = newGame.copy(
        playersVal = TreeMap(
          pID -> ClassicPlayerImpl( pID, Green, "A" ).addResourceCard( Wood ),
          pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ).addResourceCard( Clay ),
          pID2 -> ClassicPlayerImpl( pID2, Yellow, "C" ).addResourceCard( Clay ),
        )( PlayerOrdering ),
        turnVal = ClassicTurnImpl( pID )
      )
      "success without next" in {
        val state = PlayerTradeState( pID2, ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 1 ), Map( pID1 -> false ) )
        val game2 = game.setState( state )
        val command = PlayerTradeDecisionCommand( decision = true, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [PlayerTradeEndState]
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
      "success with next" in {
        val state = PlayerTradeState( pID1, ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 1 ), Map.empty )
        val game2 = game.setState( state )
        val command = PlayerTradeDecisionCommand( decision = true, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [PlayerTradeState]
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
    }
    "RobberStealCommand" should {
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val pID2 = new PlayerID( 2 )
      val nextState = ActionState()
      val state = RobberStealState( List( pID1, pID2 ), nextState )
      val game = newGame.copy(
        stateVal = state,
        playersVal = TreeMap(
          pID -> ClassicPlayerImpl( pID, Green, "A" ),
          pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ),
          pID2 -> ClassicPlayerImpl( pID2, Yellow, "C" ).addResourceCard( Wood ),
        )( PlayerOrdering ),
        turnVal = ClassicTurnImpl( pID )
      )
      "fail because of steal player has no adjacent structure" in {
        RobberStealCommand( pID1, state ).doStep( game ) shouldBe
          Failure( NoAdjacentStructure )
      }
      "success without stealing" in {
        val vertex = game.gameField.adjacentVertices( game.gameField.robberHex ).head
        val game2 = game.setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID1 ) ) ) ) )
        val command = RobberStealCommand( pID1, state )
        val undoRes1 = command.undoStep( game2 )
        undoRes1.state shouldBe state
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._1.state shouldBe nextState
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
      "success with stealing" in {
        val vertex = game.gameField.adjacentVertices( game.gameField.robberHex ).head
        val game2 = game.setGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID2 ) ) ) ) )
        val command = RobberStealCommand( pID2, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._1.state shouldBe nextState
        res.get._1.player.resources shouldBe ResourceCards.of( wood = 1 )
        res.get._1.player( pID1 ).resources shouldBe ResourceCards.of()
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player.resources shouldBe ResourceCards.of()
        undoRes.player( pID2 ).resources shouldBe ResourceCards.of( wood = 1 )
      }
    }
    "RollDicesCommand" should {
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val pID2 = new PlayerID( 2 )
      val state = ActionState()
      val game = newGame.copy(
        stateVal = state,
        playersVal = TreeMap(
          pID -> ClassicPlayerImpl( pID, Green, "A" ),
          pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ),
          pID2 -> ClassicPlayerImpl( pID2, Yellow, "C" ),
        )( PlayerOrdering ),
        turnVal = ClassicTurnImpl( pID )
      )
      "success with seven with no card drop" in {
        val game2 = game.copy( roundVal = 9 )
        val command = RollDicesCommand( state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[DiceInfo]]
        res.get._1.state shouldBe a [RobberPlaceState]
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
      "success with seven with card drop" in {
        val game2 = game.copy( roundVal = 9 ).updatePlayer( game.player.addResourceCard( Wood, game.maxHandCards + 1 ) )
        val command = RollDicesCommand( state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[DiceInfo]]
        res.get._1.state shouldBe DropHandCardsState( pID )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
      "success with gather" in {
        val hex = game.gameField.findHex( 25 ).get
        val vertices = game.gameField.adjacentVertices( hex )
        val game2 = game.copy(
          roundVal = 1,
          resourceStack = game.resourceStack.updated( Clay, 1 ),
          gameFieldVal = game.gameField.update( vertices.head.setBuilding( Some( Settlement( pID ) ) ) )
            .update( vertices( 1 ).setBuilding( Some( City( pID1 ) ) ) )
        )
        val command = RollDicesCommand( state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[GatherInfo]]
        res.get._1.state shouldBe ActionState()
        res.get._1.player( pID ).resources shouldBe ResourceCards.of()
        res.get._1.player( pID1 ).resources shouldBe ResourceCards.of( clay = 1 )
        res.get._1.player( pID2 ).resources shouldBe ResourceCards.of()
        res.get._1.asInstanceOf[ClassicGameImpl].resourceStack shouldBe game2.resourceStack.updated( Clay, 0 )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player( pID ).resources shouldBe ResourceCards.of()
        undoRes.player( pID1 ).resources shouldBe ResourceCards.of()
        undoRes.player( pID2 ).resources shouldBe ResourceCards.of()
        undoRes.asInstanceOf[ClassicGameImpl].resourceStack shouldBe game2.resourceStack
      }
    }
    "SetBeginnerCommand" should {
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val game = newGame.copy(
        playersVal = TreeMap(
          pID -> ClassicPlayerImpl( pID, Green, "A" ),
          pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ),
        )( PlayerOrdering ),
      )
      "fail because of no unique beginner specified" in {
        val state = InitBeginnerState()
        SetBeginnerCommand( state ).doStep( game ) shouldBe
          Failure( NoUniqueBeginner )
      }
      "success" in {
        val state = InitBeginnerState( Some( pID ) )
        val command = SetBeginnerCommand( state )
        val undoRes1 = command.undoStep( game )
        undoRes1.state shouldBe state
        val res = command.doStep( game )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe BuildInitSettlementState()
        res.get._1.turn shouldBe ClassicTurnImpl( pID )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.turn shouldBe game.turn
      }
    }
    "SetBuildStateCommand" should {
      val state = ActionState()
      val pID = new PlayerID( 0 )
      val game = newGame.copy(
        playersVal = newGame.playersVal + ( pID -> ClassicPlayerImpl( pID, Green, "A" ) ),
        turnVal = ClassicTurnImpl( pID )
      )
      "fail because of unavailable placement" in {
        object TestPlacement extends StructurePlacement( "Test", 1, ResourceCards.of() ) {
          override protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean ):Try[Game] = Success( game )

          override def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean ):List[PlacementPoint] = List.empty
        }
        SetBuildStateCommand( TestPlacement, state ).doStep( game ) shouldBe
          Failure( UnavailableStructure( TestPlacement ) )
      }
      "fail because of insufficient structures" in {
        val p = game.player.asInstanceOf[ClassicPlayerImpl]
        val game2 = game.updatePlayer( p.copy( structures = p.structures.updated( RoadPlacement, 0 ) ) )
        SetBuildStateCommand( RoadPlacement, state ).doStep( game2 ) shouldBe
          Failure( InsufficientStructures( RoadPlacement ) )
      }
      "fail because of no placement points" in {
        SetBuildStateCommand( RoadPlacement, state ).doStep( game ) shouldBe
          Failure( NoPlacementPoints( RoadPlacement ) )
      }
      "fail because of insufficient resources" in {
        val edge = game.gameField.edgeList.head
        val game2 = game.setGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) ) )
        SetBuildStateCommand( RoadPlacement, state ).doStep( game2 ) shouldBe
          Failure( InsufficientResources )
      }
      "success" in {
        val edge = game.gameField.edgeList.head
        val game2 = game.setGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) ) )
          .updatePlayer( game.player.addResourceCards( RoadPlacement.resources ) )
        val command = SetBuildStateCommand( RoadPlacement, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[LostResourcesInfo]]
        res.get._1.state shouldBe BuildState( RoadPlacement )
        res.get._1.player.resources shouldBe ResourceCards.of()
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player.resources shouldBe game2.player.resources
      }
    }
    "SetInitBeginnerStateCommand" should {
      val state = InitPlayerState()
      "fail because of not enough players" in {
        SetInitBeginnerStateCommand( state ).doStep( newGame ) shouldBe
          Failure( NotEnoughPlayers )
      }
      "success" in {
        val game = ( 1 to newGame.minPlayers ).red( newGame, ( g:ClassicGameImpl, i ) => {
          val pID = new PlayerID( i )
          g.copy( playersVal = g.playersVal + ( pID -> ClassicPlayerImpl( pID, Green, i.toString ) ) )
        } )
        val command = SetInitBeginnerStateCommand( state )
        val res = command.doStep( game )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe InitBeginnerState()
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
    }
    "SetPlayerTradeStateCommand" should {
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val pID2 = new PlayerID( 2 )
      val state = ActionState()
      val game = newGame.copy(
        stateVal = state,
        playersVal = TreeMap(
          pID -> ClassicPlayerImpl( pID, Green, "A" ),
          pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ),
          pID2 -> ClassicPlayerImpl( pID2, Yellow, "C" ),
        )( PlayerOrdering ),
        turnVal = ClassicTurnImpl( pID )
      )
      "fail because of insufficient resources" in {
        SetPlayerTradeStateCommand( ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 1 ), state ).doStep( game ) shouldBe
          Failure( InsufficientResources )
      }
      "success without decisions" in {
        val game2 = game.updatePlayer( game.player.addResourceCard( Wood ) )
        val command = SetPlayerTradeStateCommand( ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 1 ), state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [PlayerTradeEndState]
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
      "success with decisions" in {
        val game2 = game.updatePlayer( game.player.addResourceCard( Wood ) ).updatePlayer( game.player( pID1 ).addResourceCard( Clay ) )
        val command = SetPlayerTradeStateCommand( ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 1 ), state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [PlayerTradeState]
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
    }
    "UseDevCardCommand" should {
      val state = ActionState()
      val pID = new PlayerID( 0 )
      val pID1 = new PlayerID( 1 )
      val game = newGame.copy(
        playersVal = newGame.playersVal + ( pID -> ClassicPlayerImpl( pID, Green, "A" ), pID1 -> ClassicPlayerImpl( pID1, Blue, "B" ) ),
        turnVal = ClassicTurnImpl( pID )
      )
      "fail because a dev card has already used in this turn" in {
        val game2 = game.setTurn( game.turn.setUsedDevCard( true ) )
        UseDevCardCommand( KnightCard, state ).doStep( game2 ) shouldBe
          Failure( AlreadyUsedDevCardInTurn )
      }
      "fail because of insufficient dev cards" in {
        UseDevCardCommand( KnightCard, state ).doStep( game ) shouldBe
          Failure( InsufficientDevCards( KnightCard ) )
      }
      "fail because this dev card was drawn in this turn" in {
        val game2 = game.setTurn( game.turn.addDrawnDevCard( KnightCard ) )
          .updatePlayer( game.player.addDevCard( KnightCard ) )
        UseDevCardCommand( KnightCard, state ).doStep( game2 ) shouldBe
          Failure( DevCardDrawnInTurn( KnightCard ) )
      }
      "success with knight card without new largest army" in {
        val game2 = game.updatePlayer( game.player.addDevCard( KnightCard ) )
        val command = UseDevCardCommand( KnightCard, state )
        val undoRes1 = command.undoStep( game2 )
        undoRes1.state shouldBe state
        undoRes1.bonusCards shouldBe game2.bonusCards
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe RobberPlaceState( state )
        res.get._1.turn.usedDevCard shouldBe true
        res.get._1.player.devCards shouldBe empty
        res.get._1.player.usedDevCards should contain theSameElementsAs Vector( KnightCard )
        res.get._1.bonusCards shouldBe game2.bonusCards
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.turn.usedDevCard shouldBe false
        undoRes.player.devCards shouldBe game2.player.devCards
        undoRes.player.usedDevCards shouldBe game2.player.usedDevCards
        undoRes.bonusCards shouldBe game2.bonusCards
      }
      "success with knight card with new largest army from empty" in {
        val p = game.player.asInstanceOf[ClassicPlayerImpl]
        val game2 = game.updatePlayer( p.copy(
          usedDevCards = ( 1 until LargestArmyCard.required ).map( _ => KnightCard ).toVector,
          devCardsVal = Vector( KnightCard )
        ) )
        val command = UseDevCardCommand( KnightCard, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe RobberPlaceState( state )
        res.get._1.turn.usedDevCard shouldBe true
        res.get._1.player.devCards shouldBe empty
        res.get._1.player.usedDevCards should contain theSameElementsAs ( 1 to LargestArmyCard.required ).map( _ => KnightCard ).toVector
        res.get._1.bonusCards( LargestArmyCard ) shouldBe Some( pID, LargestArmyCard.required )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.turn.usedDevCard shouldBe false
        undoRes.player.devCards shouldBe game2.player.devCards
        undoRes.player.usedDevCards shouldBe game2.player.usedDevCards
        undoRes.bonusCards shouldBe game2.bonusCards
      }
      "success with knight card with new largest army from defined" in {
        val p = game.player.asInstanceOf[ClassicPlayerImpl]
        val game2 = game.updatePlayer( p.copy(
          usedDevCards = ( 0 until LargestArmyCard.required ).map( _ => KnightCard ).toVector,
          devCardsVal = Vector( KnightCard )
        ) ).setBonusCard( LargestArmyCard, Some( pID1, LargestArmyCard.required ) )
        val command = UseDevCardCommand( KnightCard, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe RobberPlaceState( state )
        res.get._1.turn.usedDevCard shouldBe true
        res.get._1.player.devCards shouldBe empty
        res.get._1.player.usedDevCards should contain theSameElementsAs ( 0 to LargestArmyCard.required ).map( _ => KnightCard ).toVector
        res.get._1.bonusCards( LargestArmyCard ) shouldBe Some( pID, LargestArmyCard.required + 1 )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.turn.usedDevCard shouldBe false
        undoRes.player.devCards shouldBe game2.player.devCards
        undoRes.player.usedDevCards shouldBe game2.player.usedDevCards
        undoRes.bonusCards shouldBe game2.bonusCards
      }
      "success with year of plenty card" in {
        val game2 = game.updatePlayer( game.player.addDevCard( YearOfPlentyCard ) )
        val command = UseDevCardCommand( YearOfPlentyCard, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe YearOfPlentyState( state )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
      "fail with road building card because of insufficient structures" in {
        val p = game.player.asInstanceOf[ClassicPlayerImpl]
        val game2 = game.updatePlayer( p.addDevCard( RoadBuildingCard )
          .copy( structures = p.structures.updated( RoadPlacement, 0 ) ) )
        UseDevCardCommand( RoadBuildingCard, state ).doStep( game2 ) shouldBe
          Failure( InsufficientStructures( RoadPlacement ) )
      }
      "fail with road building card because there are no placement points" in {
        val game2 = game.updatePlayer( game.player.addDevCard( RoadBuildingCard ) )
        UseDevCardCommand( RoadBuildingCard, state ).doStep( game2 ) shouldBe
          Failure( NoPlacementPoints( RoadPlacement ) )
      }
      "success with road building card" in {
        val game2 = game.updatePlayer( game.player.addDevCard( RoadBuildingCard ) )
          .setGameField( game.gameField.update( game.gameField.edgeList.head.setRoad( Some( Road( pID ) ) ) ) )
        val command = UseDevCardCommand( RoadBuildingCard, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe DevRoadBuildingState( state )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
      "success with monopoly card" in {
        val game2 = game.updatePlayer( game.player.addDevCard( MonopolyCard ) )
        val command = UseDevCardCommand( MonopolyCard, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe MonopolyState( state )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
      }
    }
    "YearOfPlentyCommand" should {
      val nextState = ActionState()
      val state = YearOfPlentyState( nextState )
      val pID = new PlayerID( 0 )
      val game = newGame.copy(
        playersVal = newGame.playersVal + ( pID -> ClassicPlayerImpl( pID, Green, "A" ) ),
        turnVal = ClassicTurnImpl( pID )
      )
      "fail because of invalid resource amount" in {
        YearOfPlentyCommand( ResourceCards.of(), state ).doStep( game ) shouldBe
          Failure( InvalidResourceAmount( 0 ) )
      }
      "success" in {
        val resources = ResourceCards.of( wood = 2 )
        val command = YearOfPlentyCommand( resources, state )
        val undoRes1 = command.undoStep( game )
        undoRes1.state shouldBe state
        val res = command.doStep( game )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[GotResourcesInfo]]
        res.get._1.state shouldBe nextState
        Success( res.get._1.asInstanceOf[ClassicGameImpl].resourceStack ) shouldBe game.resourceStack.subtract( resources )
        res.get._1.player.resources shouldBe resources
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.asInstanceOf[ClassicGameImpl].resourceStack shouldBe game.resourceStack
        undoRes.player.resources shouldBe game.player.resources
      }
    }
  }
}
