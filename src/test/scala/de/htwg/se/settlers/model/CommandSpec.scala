package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.GameField.Edge
import de.htwg.se.settlers.model.Player.{Blue, Green, Yellow}
import de.htwg.se.settlers.model.commands._
import de.htwg.se.settlers.model.state._
import de.htwg.se.settlers.util._
import org.scalatest.{Matchers, WordSpec}

import scala.collection.immutable.TreeMap
import scala.util.{Failure, Success}

class CommandSpec extends WordSpec with Matchers {
  "Command" when {
    val newGame = Game( test = true )
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
        val tooLongName = "".toLength( Game.maxPlayerNameLength + 1 )
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
        val game3 = game.copy( players = TreeMap( ( 0 until Game.maxPlayers - 1 ).map( i => {
          val pID = new PlayerID( i )
          (pID, Player( pID, Blue, i.toString ))
        } ).toArray:_* )( PlayerOrdering ) )
        val res = command.doStep( game3 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.players should have size Game.maxPlayers
        res.get._1.players.last._2.name shouldBe name
        res.get._1.players.last._2.color shouldBe Green
        val undoRed = command.undoStep( res.get._1 )
        undoRed.state shouldBe state
        undoRed.players should have size Game.maxPlayers - 1
      }
    }
    "BankTradeCommand" should {
      val playerResources = ResourceCards.of( wood = 5, clay = 3, sheep = 8, wheat = 9 )
      val pID = new PlayerID( 0 )
      val game = newGame.copy( 
        state = ActionState(),
        players = newGame.players + ( pID -> Player( pID, Green, "A", resources = playerResources ) ),
        turn = Turn( pID )
      )
      "fail because of insufficient structures" in {
        val game2 = game.updatePlayer( game.player.copy( structures = game.player.structures.updated( Road, 0 ) ) )
        BuildCommand( 0, BuildState( Road ) ).doStep( game2 ) shouldBe
          Failure( InsufficientStructures( Road ) )
      }
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
      val game = newGame.copy(
        players = newGame.players + ( pID -> Player( pID, Green, "A" ) ),
        turn = Turn( pID )
      )
      "fail road because of non existent placement point" in {
        BuildCommand( -1, BuildState( Road ) ).doStep( game ) shouldBe
          Failure( NonExistentPlacementPoint( -1 ) )
      }
      "fail road because of placement point is not empty" in {
        val edge = game.gameField.edges.head._2
        val game2 = game.updateGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) ) )
        BuildCommand( edge.id, BuildState( Road ) ).doStep( game2 ) shouldBe
          Failure( PlacementPointNotEmpty( edge.id ) )
      }
      "fail road because of no connected structures" in {
        val edge = game.gameField.edges.head._2
        BuildCommand( edge.id, BuildState( Road ) ).doStep( game ) shouldBe
          Failure( NoConnectedStructures( edge.id ) )
      }
      "success road" in {
        val edge = game.gameField.edges.head._2
        val edge2 = game.gameField.adjacentEdges( edge ).head
        val game2 = game.updateGameField( game.gameField.update( edge2.setRoad( Some( Road( pID ) ) ) ) )
        val command = BuildCommand( edge.id, BuildState( Road ) )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[BuiltInfo]]
        res.get._1.state shouldBe a [ActionState]
        res.get._1.player.structures( Road ) shouldBe Road.available - 1
        val resEdge = res.get._1.gameField.findEdge( edge.id )
        resEdge shouldNot be( None )
        resEdge.get.road shouldBe Some( Road( pID ) )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe a [BuildState]
        undoRes.player.structures( Road ) shouldBe Road.available
        val undoEdge = undoRes.gameField.findEdge( edge.id )
        undoEdge shouldNot be( None )
        undoEdge.get.road shouldBe None
      }
      "success road with new bonus card" in {
        val (game2, edge) = ( 1 until LongestRoadCard.minimumRoads ).red( (game, game.gameField.edges.head._2),
          ( data:(Game, Edge), _ ) => { (
            data._1.updateGameField( data._1.gameField.update( data._2.setRoad( Some( Road( pID ) ) ) ) ),
            data._1.gameField.adjacentEdges( data._2 ).find( _.road.isEmpty ).get
          ) } )
        val command = BuildCommand( edge.id, BuildState( Road ) )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._1.bonusCards( LongestRoadCard ) shouldBe Some( pID, LongestRoadCard.minimumRoads )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.bonusCards( LongestRoadCard ) shouldBe None
      }
      "fail settlement because of non existent placement point" in {
        BuildCommand( -1, BuildState( Settlement ) ).doStep( game ) shouldBe
          Failure( NonExistentPlacementPoint( -1 ) )
      }
      "fail settlement because of placement point is not empty" in {
        val vertex = game.gameField.vertices.head._2
        val game2 = game.updateGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        BuildCommand( vertex.id, BuildState( Settlement ) ).doStep( game2 ) shouldBe
          Failure( PlacementPointNotEmpty( vertex.id ) )
      }
      "fail settlement because of no connected structures" in {
        val vertex = game.gameField.vertices.head._2
        BuildCommand( vertex.id, BuildState( Settlement ) ).doStep( game ) shouldBe
          Failure( NoConnectedStructures( vertex.id ) )
      }
      "fail settlement because of too close to building" in {
        val vertex = game.gameField.vertices.head._2
        val edge = game.gameField.adjacentEdges( vertex ).find( e => game.gameField.adjacentVertices( e ).size > 1 )
        edge shouldNot be( None )
        val vertex2 = game.gameField.adjacentVertices( edge.get ).filter( _ != vertex ).head
        val game2 = game.updateGameField( game.gameField.update( edge.get.setRoad( Some( Road( pID ) ) ) )
            .update( vertex2.setBuilding( Some( Settlement( pID ) ) ) ) )
        BuildCommand( vertex.id, BuildState( Settlement ) ).doStep( game2 ) shouldBe
          Failure( TooCloseToBuilding( vertex.id ) )
      }
      "success settlement" in {
        val vertex = game.gameField.vertices.head._2
        val edge = game.gameField.adjacentEdges( vertex ).head
        val game2 = game.updateGameField( game.gameField.update( edge.setRoad( Some( Road( pID ) ) ) ) )
        val command = BuildCommand( vertex.id, BuildState( Settlement ) )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[BuiltInfo]]
        res.get._1.state shouldBe a [ActionState]
        res.get._1.player.structures( Settlement ) shouldBe Settlement.available - 1
        val resVertex = res.get._1.gameField.findVertex( vertex.id )
        resVertex shouldNot be( None )
        resVertex.get.building shouldBe Some( Settlement( pID ) )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe a [BuildState]
        undoRes.player.structures( Settlement ) shouldBe Settlement.available
        val undoVertex = undoRes.gameField.findVertex( vertex.id )
        undoVertex shouldNot be( None )
        undoVertex.get.building shouldBe None
      }
      "fail city because of non existent placement point" in {
        BuildCommand( -1, BuildState( City ) ).doStep( game ) shouldBe
          Failure( NonExistentPlacementPoint( -1 ) )
      }
      "fail city because of settlement required" in {
        val vertex = game.gameField.vertices.head._2
        BuildCommand( vertex.id, BuildState( City ) ).doStep( game ) shouldBe
          Failure( SettlementRequired( vertex.id ) )
      }
      "fail city because of invalid placement point" in {
        val vertex = game.gameField.vertices.head._2
        vertex shouldNot be( None )
        val game2 = game.updateGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( new PlayerID( 1 ) ) ) ) ) )
        BuildCommand( vertex.id, BuildState( City ) ).doStep( game2 ) shouldBe
          Failure( InvalidPlacementPoint( vertex.id ) )
      }
      "success city" in {
        val vertex = game.gameField.vertices.head._2
        val game2 = game.updateGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
          .updatePlayer( game.player.copy( structures = game.player.structures.updated( Settlement, Settlement.available - 1 ) ) )
        val command = BuildCommand( vertex.id, BuildState( City ) )
        val res = command.doStep( game2 )
        res shouldNot be( None )
        res.get._2 shouldBe a [Some[BuiltInfo]]
        res.get._1.state shouldBe a [ActionState]
        res.get._1.player.structures( City ) shouldBe City.available - 1
        res.get._1.player.structures( Settlement ) shouldBe Settlement.available
        val resVertex = res.get._1.gameField.findVertex( vertex.id )
        resVertex shouldNot be( None )
        resVertex.get.building shouldBe Some( City( pID ) )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe a [BuildState]
        undoRes.player.structures( City ) shouldBe City.available
        undoRes.player.structures( Settlement ) shouldBe Settlement.available - 1
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
        state = state,
        gameField = newGame.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ),
        players = TreeMap(
          pID -> Player( pID, Green, "A" ),
          pID1 -> Player( pID1, Blue, "B" ),
          pID2 -> Player( pID2, Yellow, "C" ),
        )( PlayerOrdering ),
        turn = Turn( pID )
      )
      "fail because of invalid placement point" in {
        BuildInitRoadCommand( 16, state ).doStep( game ) shouldBe
          Failure( InvalidPlacementPoint( 16 ) )
      }
      "fail because of build failure" in {
        val game2 = game.updatePlayer( game.player.copy( structures = game.player.structures.updated( Road, 0 ) ) )
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
        undoRes.player.structures( Road ) shouldBe Road.available
      }
      "success with same turn" in {
        val vertex1 = game.gameField.findVertex( 2 )
        vertex1 shouldNot be( None )
        val vertex2 = game.gameField.findVertex( 5 )
        vertex2 shouldNot be( None )
        val state2 = BuildInitRoadState( 5 )
        val game2 = game.copy(
          turn = Turn( pID2 ),
          state = state2,
          gameField = game.gameField.update( vertex1.get.setBuilding( Some( Settlement( pID1 ) ) ) )
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
        undoRes.player.structures( Road ) shouldBe Road.available
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
          turn = Turn( pID2 ),
          state = state2,
          gameField = game.gameField.update( vertex1.get.setBuilding( Some( Settlement( pID1 ) ) ) )
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
        undoRes.player.structures( Road ) shouldBe Road.available
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
          turn = Turn( pID ),
          state = state2,
          gameField = game.gameField.update( vertex1.get.setBuilding( Some( Settlement( pID1 ) ) ) )
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
        undoRes.player.structures( Road ) shouldBe Road.available
      }
    }
    "BuildInitSettlementCommand" should {
      val state = BuildInitSettlementState()
      val pID = new PlayerID( 0 )
      val game = newGame.copy(
        state = state,
        turn = Turn( pID ),
        players = newGame.players + ( pID -> Player( pID, Green, "A" ) )
      )
      "fail because of build failure" in {
        val vertex = game.gameField.vertices.head._2
        val game2 = game.updateGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        BuildInitSettlementCommand( vertex.id, state ).doStep( game2 ) shouldBe a [Failure[_]]
      }
      "success without resource cards" in {
        val vertex = game.gameField.vertices.head._2
        val command = BuildInitSettlementCommand( vertex.id, state )
        val res = command.doStep( game )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe BuildInitRoadState( vertex.id )
        res.get._1.player.structures( Settlement ) shouldBe Settlement.available - 1
        val resVertex = res.get._1.gameField.findVertex( vertex.id )
        resVertex shouldNot be( None )
        resVertex.get.building shouldBe Some( Settlement( pID ) )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player.structures( Road ) shouldBe Road.available
        val undoVertex = undoRes.gameField.findVertex( vertex.id )
        undoVertex shouldNot be( None )
        undoVertex.get.building shouldBe None
      }
      "success with resource cards" in {
        val vertex = game.gameField.findVertex( 1 )
        vertex shouldNot be( None )
        val vertex2 = game.gameField.findVertex( 3 )
        vertex2 shouldNot be( None )
        val game2 = game.updateGameField( game.gameField.update( vertex2.get.setBuilding( Some( Settlement( pID ) ) ) ) )
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
        state = state,
        turn = Turn( pID ),
        players = newGame.players + ( pID -> Player( pID, Green, "A" ) )
      )
      "fail" in {
        BuyDevCardCommand( state ).doStep( game ) shouldBe a [Failure[_]]
      }
      "success" in {
        val game2 = game.updatePlayer( game.player.addResourceCards( Cards.developmentCardCost ) )
        val command = BuyDevCardCommand( state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[DrawnDevCardInfo]]
        res.get._1.player.devCards should have size 1
        res.get._1.player.resources shouldBe ResourceCards.of()
        res.get._1.turn.drawnDevCards should have size 1
        res.get._1.developmentCards should have size game2.developmentCards.size - 1
        val undoRes = command.undoStep( res.get._1 )
        undoRes.developmentCards shouldBe game2.developmentCards
        undoRes.player.devCards shouldBe empty
        undoRes.turn.drawnDevCards shouldBe empty
        undoRes.player.resources.filter( _._2 > 0 ) shouldBe Cards.developmentCardCost
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
        state = state,
        turn = Turn( pID ),
        players = newGame.players + ( pID -> Player( pID, Green, "A" ) )
      )
      "fail because of build failure" in {
        val edge = game.gameField.edges.head._2
        DevBuildRoadCommand( edge.id, state ).doStep( game ) shouldBe a [Failure[_]]
      }
      "success with first road" in {
        val vertex = game.gameField.vertices.head._2
        val edge = game.gameField.adjacentEdges( vertex ).filter( e => e.h1.isLand || e.h2.isLand ).head
        val game2 = game.updateGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        val command = DevBuildRoadCommand( edge.id, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[BuiltInfo]]
        res.get._1.state shouldBe DevRoadBuildingState( nextState, 1 )
        res.get._1.player.structures( Road ) shouldBe Road.available - 1
        val resEdge = res.get._1.gameField.findEdge( edge.id )
        resEdge shouldNot be( None )
        resEdge.get.road shouldBe Some( Road( pID ) )
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player.structures( Road ) shouldBe Road.available
        val undoEdge = undoRes.gameField.findEdge( edge.id )
        undoEdge shouldNot be( None )
        undoEdge.get.road shouldBe None
      }
      "success with second road" in {
        val vertex = game.gameField.vertices.head._2
        val edge = game.gameField.adjacentEdges( vertex ).filter( e => e.h1.isLand || e.h2.isLand ).head
        val state2 = DevRoadBuildingState( nextState, 1 )
        val game2 = game.setState( state2 )
          .updateGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        val command = DevBuildRoadCommand( edge.id, state2 )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[BuiltInfo]]
        res.get._1.state shouldBe nextState
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state2
      }
      "success with insufficient structures info" in {
        val vertex = game.gameField.vertices.head._2
        val edge = game.gameField.adjacentEdges( vertex ).filter( e => e.h1.isLand || e.h2.isLand ).head
        val game2 = game.updatePlayer( game.player.copy( structures = game.player.structures.updated( Road, 1 ) ) )
          .updateGameField( game.gameField.update( vertex.setBuilding( Some( Settlement( pID ) ) ) ) )
        val command = DevBuildRoadCommand( edge.id, state )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe a [Some[InsufficientStructuresInfo]]
        res.get._1.state shouldBe nextState
        res.get._1.player.structures( Road ) shouldBe 0
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.player.structures( Road ) shouldBe 1
      }
      "success with no placement points info" in {
        val edge = game.gameField.findEdge( 1 )
        val vertex = game.gameField.adjacentVertices( edge.get ).head
        val pID1 = new PlayerID( 1 )
        val game2 = game.updateGameField( game.gameField.adjacentEdges( edge.get ).red( game.gameField, ( g:GameField, e:Edge ) =>
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
        players = TreeMap(
          pID -> Player( pID, Green, "A" ),
          pID1 -> Player( pID1, Blue, "B" ),
          pID2 -> Player( pID2, Yellow, "C" ),
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
        players = newGame.players + ( pID -> Player( pID, Green, "A" ).addResourceCards( ResourceCards.of( wood = Game.maxHandCards + 1 ) ) ),
        turn = Turn( pID )
      )
      "fail because of invalid resource amount" in {
        val state = DropHandCardsState( pID )
        val game2 = game.setState( state )
        val dropResources = ResourceCards.of( wood = Game.maxHandCards + 10 )
        DropHandCardsCommand( state, dropResources ).doStep( game2 ) shouldBe
          Failure( InvalidResourceAmount( dropResources.amount ) )
      }
      "success with next" in {
        val state = DropHandCardsState( pID )
        val pID1 = new PlayerID( 1 )
        val game2 = game.copy(
          state = state,
          players = game.players + ( pID1 -> Player( pID1, Blue, "B" ).addResourceCards( ResourceCards.of( clay = Game.maxHandCards + 1 ) ) )
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
        players = TreeMap(
          pID -> Player( pID, Green, "A" ),
          pID1 -> Player( pID1, Blue, "B" ),
        )( PlayerOrdering ),
        turn = Turn( pID )
      )
      "success" in {
        val state = ActionState()
        val game2 = game.setState( state )
        val command = EndTurnCommand( state )
        val undoRes1 = command.undoStep( game2 )
        undoRes1.turn shouldBe Turn( pID1 )
        val res = command.doStep( game2 )
        res shouldBe a [Success[_]]
        res.get._2 shouldBe None
        res.get._1.state shouldBe a [NextPlayerState]
        res.get._1.turn shouldBe Turn( pID1 )
        res.get._1.round shouldBe game2.round + 1
        val undoRes = command.undoStep( res.get._1 )
        undoRes.state shouldBe state
        undoRes.turn shouldBe Turn( pID )
        undoRes.round shouldBe game2.round
      }
    }
  }
}