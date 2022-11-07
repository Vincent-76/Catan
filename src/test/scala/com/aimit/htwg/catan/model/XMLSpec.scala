package com.aimit.htwg.catan.model

import com.google.inject.{ Guice, Injector }
import com.aimit.htwg.catan.{ CatanModule, ClassicCatanModule }
import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model.commands._
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.XMLMap
import com.aimit.htwg.catan.model.impl.fileio.{ XMLDeserializer, XMLSerializable }
import com.aimit.htwg.catan.model.impl.game.ClassicGameImpl
import com.aimit.htwg.catan.model.impl.gamefield.ClassicGameFieldImpl
import com.aimit.htwg.catan.model.impl.placement.{ CityPlacement, RoadPlacement, SettlementPlacement }
import com.aimit.htwg.catan.model.impl.player.ClassicPlayerImpl
import com.aimit.htwg.catan.model.impl.turn.ClassicTurnImpl
import com.aimit.htwg.catan.model.state._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class XMLSpec extends AnyWordSpec with Matchers {
  CatanModule.init()
  val injector:Injector = Guice.createInjector( ClassicCatanModule( test = true ) )
  "(de)serialized" when {
    "Model" should {
      "Port" in {
        Port().check( Port )
      }
      "WaterArea" in {
        WaterArea().check( WaterArea )
      }
      "DesertArea" in {
        DesertArea().check( DesertArea )
      }
      "ResourceArea" in {
        ResourceArea( Wood, Six ).check( ResourceArea )
      }
      "ResourceCards" in {
        val r = ResourceCards.of( wood = 1 )
        val xml = r.toXML( _.title, _.toString )
        ResourceCards.fromXML( xml ) shouldBe r
      }
      "ClassicGameImpl" in {
        val game = ClassicGameImpl(
          injector.getInstance( classOf[GameField] ),
          injector.getInstance( classOf[Turn] ), 1,
          injector.getInstance( classOf[PlayerFactory] ),
          "ClassicPlayerImpl",
          availablePlacementsVal = List( RoadPlacement, SettlementPlacement, CityPlacement ),
        ).addPlayer( Green, "Test" )
          .setBonusCard( LongestRoadCard, Some( PlayerID( 0 ), 6 ) )
          .setWinner( PlayerID( 0 ) )
        val xml = game.toXML
        ClassicGameImpl.fromXML( xml ).copy( playerFactory = null ) shouldBe game.copy( playerFactory = null )
      }
      "Hex" in {
        Hex( 0, 0, 0, DesertArea() ).check( Hex )
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
        val xml = edge.toXML
        Edge.fromXML( xml, hexList ) shouldBe edge
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
        val xml = vertex.toXML
        Vertex.fromXML( xml, hexList ) shouldBe vertex
      }
      "ClassicGameFieldImpl" in {
        ClassicGameFieldImpl( 1 ).asInstanceOf[GameField].check( ClassicGameFieldImpl )
      }
      "PlayerID" in {
        PlayerID( 1 ).check( PlayerID )
      }
      "ClassicPlayerImpl" in {
        ClassicPlayerImpl( PlayerID( 0 ), Green, "Test", devCardsVal = Vector( KnightCard ), usedDevCards = Vector( KnightCard ) )
          .asInstanceOf[Player].check( ClassicPlayerImpl )
      }
      "Road" in {
        Road( PlayerID( 1 ) ).asInstanceOf[Structure].check( Road )
      }
      "Settlement" in {
        Settlement( PlayerID( 1 ) ).asInstanceOf[Structure].check( Settlement )
      }
      "City" in {
        City( PlayerID( 1 ) ).asInstanceOf[Structure].check( City )
      }
      "ClassicTurnImpl" in {
        ClassicTurnImpl( drawnDevCardsVal = List( KnightCard ) ).asInstanceOf[Turn].check( ClassicTurnImpl )
      }
    }
    "State" should {
      "ActionState" in {
        ActionState().asInstanceOf[State].check( ActionState )
      }
      "BuildInitRoadState" in {
        BuildInitRoadState( 1 ).asInstanceOf[State].check( BuildInitRoadState )
      }
      "BuildInitSettlementState" in {
        BuildInitSettlementState().asInstanceOf[State].check( BuildInitSettlementState )
      }
      "BuildState" in {
        BuildState( RoadPlacement ).asInstanceOf[State].check( BuildState )
      }
      "DevRoadBuildingState" in {
        DevRoadBuildingState( ActionState() ).asInstanceOf[State].check( DevRoadBuildingState )
      }
      "DiceState" in {
        DiceState().asInstanceOf[State].check( DiceState )
      }
      "DropHandCardsState" in {
        DropHandCardsState( PlayerID( 0 ), List( PlayerID( 0 ) ) ).asInstanceOf[State].check( DropHandCardsState )
      }
      "InitBeginnerState" in {
        InitBeginnerState(
          beginner = Some( PlayerID( 0 ) ),
          diceValues = Map( PlayerID( 0 ) -> 3 )
        ).asInstanceOf[State].check( InitBeginnerState )
      }
      "InitPlayerState" in {
        InitPlayerState().asInstanceOf[State].check( InitPlayerState )
      }
      "InitState" in {
        InitState().asInstanceOf[State].check( InitState )
      }
      "MonopolyState" in {
        MonopolyState( ActionState() ).asInstanceOf[State].check( MonopolyState )
      }
      "NextPlayerState" in {
        NextPlayerState().asInstanceOf[State].check( NextPlayerState )
      }
      "PlayerTradeEndState" in {
        PlayerTradeEndState(
          ResourceCards.of( wood = 1 ),
          ResourceCards.of( clay = 1 ),
          Map( PlayerID( 0 ) -> true )
        ).asInstanceOf[State].check( PlayerTradeEndState )
      }
      "PlayerTradeState" in {
        PlayerTradeState(
          PlayerID( 0 ),
          ResourceCards.of( wood = 1 ),
          ResourceCards.of( clay = 1 ),
          Map( PlayerID( 0 ) -> true )
        ).asInstanceOf[State].check( PlayerTradeState )
      }
      "RobberPlaceState" in {
        RobberPlaceState( ActionState() ).asInstanceOf[State].check( RobberPlaceState )
      }
      "RobberStealState" in {
        RobberStealState( List( PlayerID( 0 ) ), ActionState() ).asInstanceOf[State].check( RobberStealState )
      }
      "YearOfPlentyState" in {
        YearOfPlentyState( ActionState() ).asInstanceOf[State].check( YearOfPlentyState )
      }
    }
    "Command" should {
      "AbortPlayerTradeCommand" in {
        AbortPlayerTradeCommand(
          PlayerTradeEndState( ResourceCards.of(), ResourceCards.of(), Map.empty )
        ).asInstanceOf[Command].check( AbortPlayerTradeCommand )
      }
      "AddPlayerCommand" in {
        AddPlayerCommand( Green, "Test", InitPlayerState() )
          .asInstanceOf[Command].check( AddPlayerCommand )
      }
      "BankTradeCommand" in {
        BankTradeCommand( ResourceCards.of(), ResourceCards.of() )
          .asInstanceOf[Command].check( BankTradeCommand )
      }
      "BuildCommand" in {
        BuildCommand( 1, BuildState( RoadPlacement ) )
          .asInstanceOf[Command].check( BuildCommand )
      }
      "BuildInitRoadCommand" in {
        BuildInitRoadCommand( 1, BuildInitRoadState( 1 ) )
          .asInstanceOf[Command].check( BuildInitRoadCommand )
      }
      "BuildInitSettlementCommand" in {
        BuildInitSettlementCommand( 1, BuildInitSettlementState() )
          .asInstanceOf[Command].check( BuildInitSettlementCommand )
      }
      "BuyDevCardCommand" in {
        BuyDevCardCommand( ActionState() )
          .asInstanceOf[Command].check( BuyDevCardCommand )
      }
      "ChangeStateCommand" in {
        ChangeStateCommand( ActionState(), NextPlayerState() )
          .asInstanceOf[Command].check( ChangeStateCommand )
      }
      "DevBuildRoadCommand" in {
        DevBuildRoadCommand( 1, DevRoadBuildingState( ActionState(), 1 ) )
          .asInstanceOf[Command].check( DevBuildRoadCommand )
      }
      "DiceOutBeginnerCommand" in {
        DiceOutBeginnerCommand( InitBeginnerState() )
          .asInstanceOf[Command].check( DiceOutBeginnerCommand )
      }
      "DropHandCardsCommand" in {
        DropHandCardsCommand( DropHandCardsState( PlayerID( 0 ) ), ResourceCards.of() )
          .asInstanceOf[Command].check( DropHandCardsCommand )
      }
      "EndTurnCommand" in {
        EndTurnCommand( ActionState() )
          .asInstanceOf[Command].check( EndTurnCommand )
      }
      "InitGameCommand" in {
        InitGameCommand()
          .asInstanceOf[Command].check( InitGameCommand )
      }
      "MonopolyCommand" in {
        MonopolyCommand( Wood, MonopolyState( ActionState() ) )
          .asInstanceOf[Command].check( MonopolyCommand )
      }
      "PlaceRobberCommand" in {
        val cmd = PlaceRobberCommand( 1, RobberPlaceState( ActionState() ) )
        cmd.robbedResource = Some( Wood )
        cmd.asInstanceOf[Command].check( PlaceRobberCommand )
      }
      "PlayerTradeCommand" in {
        PlayerTradeCommand( PlayerID( 1 ), PlayerTradeEndState( ResourceCards.of(), ResourceCards.of(), Map.empty ) )
          .asInstanceOf[Command].check( PlayerTradeCommand )
      }
      "PlayerTradeDecisionCommand" in {
        PlayerTradeDecisionCommand( decision = true, PlayerTradeState( PlayerID( 0 ), ResourceCards.of(), ResourceCards.of(), Map.empty ) )
          .asInstanceOf[Command].check( PlayerTradeDecisionCommand )
      }
      "RobberStealCommand" in {
        val cmd = RobberStealCommand( PlayerID( 1 ), RobberStealState( List.empty, ActionState() ) )
        cmd.robbedResource = Some( Wood )
        cmd.asInstanceOf[Command].check( RobberStealCommand )
      }
      "RollDicesCommand" in {
        RollDicesCommand( ActionState() )
          .asInstanceOf[Command].check( RollDicesCommand )
      }
      "SetBeginnerCommand" in {
        SetBeginnerCommand( InitBeginnerState() )
          .asInstanceOf[Command].check( SetBeginnerCommand )
      }
      "SetBuildStateCommand" in {
        SetBuildStateCommand( RoadPlacement, ActionState() )
          .asInstanceOf[Command].check( SetBuildStateCommand )
      }
      "SetInitBeginnerStateCommand" in {
        SetInitBeginnerStateCommand( ActionState() )
          .asInstanceOf[Command].check( SetInitBeginnerStateCommand )
      }
      "SetPlayerTradeStateCommand" in {
        SetPlayerTradeStateCommand( ResourceCards.of(), ResourceCards.of(), ActionState() )
          .asInstanceOf[Command].check( SetPlayerTradeStateCommand )
      }
      "UseDevCardCommand" in {
        UseDevCardCommand( KnightCard, ActionState() )
          .asInstanceOf[Command].check( UseDevCardCommand )
      }
      "YearOfPlentyCommand" in {
        YearOfPlentyCommand( ResourceCards.of(), YearOfPlentyState( ActionState() ) )
          .asInstanceOf[Command].check( YearOfPlentyCommand )
      }
    }
  }

  implicit class XMLTestSerializable[T <: XMLSerializable]( obj:T ) {
    def check( deserializer:XMLDeserializer[T] ):Unit = {
      val xml = obj.toXML
      val test = xml.toString
      deserializer.fromXML( xml ) shouldBe obj
    }
  }

}
