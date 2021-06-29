package de.htwg.se.catan.model

import com.google.inject.{ Guice, Injector }
import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLMap
import de.htwg.se.catan.model.impl.fileio.{ XMLDeserializer, XMLSerializable }
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.model.impl.placement.{ CityPlacement, RoadPlacement, SettlementPlacement }
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import de.htwg.se.catan.model.impl.turn.ClassicTurnImpl
import de.htwg.se.catan.model.state._
import org.scalatest.{ Matchers, WordSpec }

class XMLSpec extends WordSpec with Matchers {
  CatanModule.init()
  val injector:Injector = Guice.createInjector( new CatanModule( test = true ) )
  "(de)serialized" when {
    "requested" should {
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
  }

  implicit class XMLTestSerializable[T <: XMLSerializable]( obj:T ) {
    def check( deserializer:XMLDeserializer[T] ):Unit = {
      val xml = obj.toXML
      val test = xml.toString
      deserializer.fromXML( xml ) shouldBe obj
    }
  }

}
