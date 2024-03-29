package com.aimit.htwg.catan.model

import Card.ResourceCards
import com.aimit.htwg.catan.CatanModule
import com.aimit.htwg.catan.model.commands._
import com.aimit.htwg.catan.model.impl.placement.RoadPlacement
import com.aimit.htwg.catan.model.state._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StateSpec extends AnyWordSpec with Matchers {
  CatanModule.init()
  "State" when {
    "ActionState" should {
      val state = ActionState()
      "setBuildState" in {
        state.setBuildState( RoadPlacement ) shouldBe a [Some[SetBuildStateCommand]]
      }
      "bankTrade" in {
        state.bankTrade( ResourceCards.of(), ResourceCards.of() ) shouldBe a [Some[BankTradeCommand]]
      }
      "setPlayerTradeState" in {
        state.setPlayerTradeState( ResourceCards.of(), ResourceCards.of() ) shouldBe a [Some[SetPlayerTradeStateCommand]]
      }
      "buyDevCard" in {
        state.buyDevCard() shouldBe a [Some[UseDevCardCommand]]
      }
      "useDevCard" in {
        state.useDevCard( KnightCard ) shouldBe a [Some[UseDevCardCommand]]
      }
      "endTurn" in {
        state.endTurn() shouldBe a [Some[EndTurnCommand]]
      }
    }
    "BuildInitRoadState" should {
      val state = BuildInitRoadState( 0 )
      "buildInitRoad" in {
        state.buildInitRoad( 0 ) shouldBe a [Some[BuildInitRoadCommand]]
      }
    }
    "BuildInitSettlement" should {
      val state = BuildInitSettlementState()
      "buildInitSettlement" in {
        state.buildInitSettlement( 0 ) shouldBe a [Some[BuildInitSettlementCommand]]
      }
    }
    "BuildState" should {
      val state = BuildState( RoadPlacement )
      "build" in {
        state.build( 0 ) shouldBe a [Some[BuildCommand]]
      }
    }
    "DevRoadBuildingState" should {
      val state = DevRoadBuildingState( ActionState(), 1 )
      "devBuildRoad" in {
        state.devBuildRoad( 0 ) shouldBe a [Some[DevBuildRoadCommand]]
      }
    }
    "DiceState" should {
      val state = DiceState()
      "useDevCard" in {
        state.useDevCard( KnightCard ) shouldBe a [Some[UseDevCardCommand]]
      }
      "rollTheDices" in {
        state.rollTheDices() shouldBe a [Some[RollDicesCommand]]
      }
    }
    "DropHandCardsState" should {
      val state = DropHandCardsState( new PlayerID( 0 ), List.empty )
      "dropResourceCardsToRobber" in {
        state.dropResourceCardsToRobber( ResourceCards.of() ) shouldBe a [Some[DropHandCardsCommand]]
      }
    }
    "InitBeginnerState" should {
      val state = InitBeginnerState( None, Map.empty, 2 )
      "diceOutBeginner" in {
        state.diceOutBeginner() shouldBe a [Some[DiceOutBeginnerCommand]]
      }
      "setBeginner" in {
        state.setBeginner() shouldBe a [Some[SetBeginnerCommand]]
      }
    }
    "InitPlayerState" should {
      val state = InitPlayerState()
      "addPlayer" in {
        state.addPlayer( Green, "A" ) shouldBe a [Some[AddPlayerCommand]]
      }
      "setInitBeginnerState" in {
        state.setInitBeginnerState() shouldBe a [Some[SetInitBeginnerStateCommand]]
      }
    }
    "InitState" should {
      val state = InitState()
      "initPlayers" in {
        state.initGame() shouldBe a [Some[InitPlayerState]]
      }
    }
    "MonopolyState" should {
      val state = MonopolyState( ActionState() )
      "monopolyAction" in {
        state.monopolyAction( Wood ) shouldBe a [Some[MonopolyCommand]]
      }
    }
    "NextPlayerState" should {
      val state = NextPlayerState()
      "startTurn" in {
        state.startTurn() shouldBe a [Some[ChangeStateCommand]]
      }
    }
    "PlayerTradeEndState" should {
      val state = PlayerTradeEndState( ResourceCards.of(), ResourceCards.of(), Map.empty )
      "playerTrade" in {
        state.playerTrade( new PlayerID( 1 ) ) shouldBe a [Some[PlayerTradeCommand]]
      }
      "abortPlayerTrade" in {
        state.abortPlayerTrade() shouldBe a [Some[AbortPlayerTradeCommand]]
      }
    }
    "PlayerTradeState" should {
      val state = PlayerTradeState( new PlayerID( 0 ), ResourceCards.of(), ResourceCards.of(), Map.empty )
      "playerTradeDecision" in {
        state.playerTradeDecision( false ) shouldBe a [Some[PlayerTradeDecisionCommand]]
      }
    }
    "RobberPlayerState" should {
      val state = RobberPlaceState( ActionState() )
      "placeRobber" in {
        state.placeRobber( 0 ) shouldBe a [Some[PlaceRobberCommand]]
      }
    }
    "RobberStealState" should {
      val state = RobberStealState( List.empty, ActionState() )
      "robberStealFromPlayer" in {
        state.robberStealFromPlayer( new PlayerID( 1 ) ) shouldBe a [Some[RobberStealCommand]]
      }
    }
    "YearOfPlentyState" should {
      val state = YearOfPlentyState( ActionState() )
      "yearOfPlentyAction" in {
        state.yearOfPlentyAction( ResourceCards.of() ) shouldBe a [Some[YearOfPlentyCommand]]
      }
    }
  }
}
