package de.htwg.se.catan.model

import com.google.inject.{ Guice, Injector }
import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.model.Card._
import de.htwg.se.catan.model.commands._
import de.htwg.se.catan.model.impl.fileio.JsonFileIO._
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import de.htwg.se.catan.model.impl.turn.ClassicTurnImpl
import de.htwg.se.catan.model.state._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{ Json, Reads, Writes }

class JsonSpec extends AnyWordSpec with Matchers {
  CatanModule.init()
  val injector:Injector = Guice.createInjector( new CatanModule( test = true ) )
  "(de)serialized" when {
    "Model" should {
      "Port" in {
        Port().check()
      }
      "WaterArea" in {
        WaterArea().asInstanceOf[Area].check()
      }
      "DesertArea" in {
        DesertArea().asInstanceOf[LandArea].check()
      }
      "ResourceArea" in {
        ResourceArea( Wood, Six ).asInstanceOf[Area].check()
      }
      "ResourceCards" in {
        val r = ResourceCards.of( wood = 1 )
        val json = r.toJson
        json.as[ResourceCards] shouldBe r
      }
      "DevelopmentCard" in {
        KnightCard.asInstanceOf[DevelopmentCard].check()
      }
      "BonusCard" in {
        LongestRoadCard.asInstanceOf[BonusCard].check()
      }
      "DiceValue" in {
        Two.asInstanceOf[DiceValue].check()
      }
      "ClassicGameImpl" in {
        val game = new ClassicGameImpl( injector.getInstance( classOf[GameField] ), injector.getInstance( classOf[Turn] ), 1, injector.getInstance( classOf[PlayerFactory] ), "ClassicPlayerImpl" )
          .setBonusCard( LongestRoadCard, Some( PlayerID( 0 ), 6 ) )
        val json = Json.toJson( game.asInstanceOf[Game] )
        json.as[Game].asInstanceOf[ClassicGameImpl].copy( playerFactory = null ) shouldBe game.copy( playerFactory = null )
      }
      "Hex" in {
        Hex( 0, 0, 0, DesertArea() ).check()
      }
      "Edge" in {
        val hexList = List(
          Hex( 0, 0, 0, DesertArea() ),
          Hex( 1, 0, 1, WaterArea() )
        )
        val edge = Edge(
          0,
          hexList( 0 ),
          hexList( 1 ),
          port = Some( Port() ),
          road = Some( Road( PlayerID( 0 ) ) )
        )
        val json = Json.toJson( edge )
        Edge.fromJson( json, hexList ) shouldBe edge
      }
      "Vertex" in {
        val hexList = List(
          Hex( 0, 0, 0, DesertArea() ),
          Hex( 1, 0, 1, WaterArea() ),
          Hex( 2, 0, 2, ResourceArea( Wood, Three ) )
        )
        val vertex = Vertex(
          0,
          hexList( 0 ),
          hexList( 1 ),
          hexList( 2 ),
          port = Some( Port() ),
          building = Some( Settlement( PlayerID( 0 ) ) )
        )
        val json = Json.toJson( vertex )
        Vertex.fromJson( json, hexList ) shouldBe vertex
      }
      "ClassicGameFieldImpl" in {
        ClassicGameFieldImpl( 1 ).asInstanceOf[GameField].check()
      }
      "RobberPlacement" in {
        RobberPlacement.asInstanceOf[Placement].check()
      }
      "RoadPlacement" in {
        RoadPlacement.asInstanceOf[StructurePlacement].check()
      }
      "SettlementPlacement" in {
        SettlementPlacement.asInstanceOf[VertexPlacement].check()
      }
      "CityPlacement" in {
        CityPlacement.asInstanceOf[VertexPlacement].check()
      }
      "PlayerID" in {
        PlayerID( 1 ).check()
      }
      "PlayerColor" in {
        Green.asInstanceOf[PlayerColor].check()
      }
      "ClassicPlayerImpl" in {
        ClassicPlayerImpl( PlayerID( 0 ), Green, "Test" ).asInstanceOf[Player].check()
      }
      "FieldType" in {
        Water.asInstanceOf[FieldType].check()
      }
      "Resource" in {
        Wood.asInstanceOf[Resource].check()
      }
      "Structure" in {
        Road( PlayerID( 1 ) ).check()
        Road( PlayerID( 1 ) ).asInstanceOf[Structure].check()
      }
      "Building" in {
        Settlement( PlayerID( 1 ) ).asInstanceOf[Building].check()
        City( PlayerID( 1 ) ).asInstanceOf[Building].check()
      }
      "ClassicTurnImpl" in {
        ClassicTurnImpl().asInstanceOf[Turn].check()
      }
    }
    "State" should {
      "ActionState" in {
        ActionState().asInstanceOf[State].check()
      }
      "BuildInitRoadState" in {
        BuildInitRoadState( 1 ).asInstanceOf[State].check()
      }
      "BuildInitSettlementState" in {
        BuildInitSettlementState().asInstanceOf[State].check()
      }
      "BuildState" in {
        BuildState( RoadPlacement ).asInstanceOf[State].check()
      }
      "DevRoadBuildingState" in {
        DevRoadBuildingState( ActionState() ).asInstanceOf[State].check()
      }
      "DiceState" in {
        DiceState().asInstanceOf[State].check()
      }
      "DropHandCardsState" in {
        DropHandCardsState( PlayerID( 0 ) ).asInstanceOf[State].check()
      }
      "InitBeginnerState" in {
        InitBeginnerState().asInstanceOf[State].check()
      }
      "InitPlayerState" in {
        InitPlayerState().asInstanceOf[State].check()
      }
      "InitState" in {
        InitState().asInstanceOf[State].check()
      }
      "MonopolyState" in {
        MonopolyState( ActionState() ).asInstanceOf[State].check()
      }
      "NextPlayerState" in {
        NextPlayerState().asInstanceOf[State].check()
      }
      "PlayerTradeEndState" in {
        PlayerTradeEndState( ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 1 ), Map.empty ).asInstanceOf[State].check()
      }
      "PlayerTradeState" in {
        PlayerTradeState( PlayerID( 0 ), ResourceCards.of( wood = 1 ), ResourceCards.of( clay = 1 ), Map.empty ).asInstanceOf[State].check()
      }
      "RobberPlaceState" in {
        RobberPlaceState( ActionState() ).asInstanceOf[State].check()
      }
      "RobberStealState" in {
        RobberStealState( List.empty, ActionState() ).asInstanceOf[State].check()
      }
      "YearOfPlentyState" in {
        YearOfPlentyState( ActionState() ).asInstanceOf[State].check()
      }
    }
    "Command" should {
      "AbortPlayerTradeCommand" in {
        AbortPlayerTradeCommand(
          PlayerTradeEndState( ResourceCards.of(), ResourceCards.of(), Map.empty )
        ).asInstanceOf[Command].check()
      }
      "AddPlayerCommand" in {
        AddPlayerCommand( Green, "Test", InitPlayerState() )
          .asInstanceOf[Command].check()
      }
      "BankTradeCommand" in {
        BankTradeCommand( ResourceCards.of(), ResourceCards.of() )
          .asInstanceOf[Command].check()
      }
      "BuildCommand" in {
        BuildCommand( 1, BuildState( RoadPlacement ) )
          .asInstanceOf[Command].check()
      }
      "BuildInitRoadCommand" in {
        BuildInitRoadCommand( 1, BuildInitRoadState( 1 ) )
          .asInstanceOf[Command].check()
      }
      "BuildInitSettlementCommand" in {
        BuildInitSettlementCommand( 1, BuildInitSettlementState() )
          .asInstanceOf[Command].check()
      }
      "BuyDevCardCommand" in {
        BuyDevCardCommand( ActionState() )
          .asInstanceOf[Command].check()
      }
      "ChangeStateCommand" in {
        ChangeStateCommand( ActionState(), NextPlayerState() )
          .asInstanceOf[Command].check()
      }
      "DevBuildRoadCommand" in {
        DevBuildRoadCommand( 1, DevRoadBuildingState( ActionState(), 1 ) )
          .asInstanceOf[Command].check()
      }
      "DiceOutBeginnerCommand" in {
        DiceOutBeginnerCommand( InitBeginnerState() )
          .asInstanceOf[Command].check()
      }
      "DropHandCardsCommand" in {
        DropHandCardsCommand( DropHandCardsState( PlayerID( 0 ) ), ResourceCards.of() )
          .asInstanceOf[Command].check()
      }
      "EndTurnCommand" in {
        EndTurnCommand( ActionState() )
          .asInstanceOf[Command].check()
      }
      "InitGameCommand" in {
        InitGameCommand()
          .asInstanceOf[Command].check()
      }
      "MonopolyCommand" in {
        MonopolyCommand( Wood, MonopolyState( ActionState() ) )
          .asInstanceOf[Command].check()
      }
      "PlaceRobberCommand" in {
        val cmd = PlaceRobberCommand( 1, RobberPlaceState( ActionState() ) )
        cmd.robbedResource = Some( Wood )
        cmd.asInstanceOf[Command].check()
      }
      "PlayerTradeCommand" in {
        PlayerTradeCommand( PlayerID( 1 ), PlayerTradeEndState( ResourceCards.of(), ResourceCards.of(), Map.empty ) )
          .asInstanceOf[Command].check()
      }
      "PlayerTradeDecisionCommand" in {
        PlayerTradeDecisionCommand( decision = true, PlayerTradeState( PlayerID( 0 ), ResourceCards.of(), ResourceCards.of(), Map.empty ) )
          .asInstanceOf[Command].check()
      }
      "RobberStealCommand" in {
        val cmd = RobberStealCommand( PlayerID( 1 ), RobberStealState( List.empty, ActionState() ) )
        cmd.robbedResource = Some( Wood )
        cmd.asInstanceOf[Command].check()
      }
      "RollDicesCommand" in {
        RollDicesCommand( ActionState() )
          .asInstanceOf[Command].check()
      }
      "SetBeginnerCommand" in {
        SetBeginnerCommand( InitBeginnerState() )
          .asInstanceOf[Command].check()
      }
      "SetBuildStateCommand" in {
        SetBuildStateCommand( RoadPlacement, ActionState() )
          .asInstanceOf[Command].check()
      }
      "SetInitBeginnerStateCommand" in {
        SetInitBeginnerStateCommand( ActionState() )
          .asInstanceOf[Command].check()
      }
      "SetPlayerTradeStateCommand" in {
        SetPlayerTradeStateCommand( ResourceCards.of(), ResourceCards.of(), ActionState() )
          .asInstanceOf[Command].check()
      }
      "UseDevCardCommand" in {
        UseDevCardCommand( KnightCard, ActionState() )
          .asInstanceOf[Command].check()
      }
      "YearOfPlentyCommand" in {
        YearOfPlentyCommand( ResourceCards.of(), YearOfPlentyState( ActionState() ) )
          .asInstanceOf[Command].check()
      }
    }
  }

  implicit class JsonTestObject[T]( obj:T )( implicit fjs:Writes[T], fjs2:Reads[T] ) {
    def check():Unit = {
      val json = Json.toJson( obj )
      json.as[T] shouldBe obj
    }
  }

}
