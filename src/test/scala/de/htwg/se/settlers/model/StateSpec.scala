package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.Player.Green
import de.htwg.se.settlers.model.commands._
import de.htwg.se.settlers.model.state._
import org.scalatest.{Matchers, WordSpec}

class StateSpec extends WordSpec with Matchers {
  "State" when {
    "ActionState" should {
      val state = ActionState()
      "setBuildState" in {
        state.setBuildState( Road ) shouldBe a [Some[SetBuildStateCommand]]
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
      val state = BuildState( Road )
      "build" in {
        state.build( 0 ) shouldBe a [Some[BuildCommand]]
      }
    }
    "DevRoadBuildingState" should {
      val state = DevRoadBuildingState( ActionState(), 0 )
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
      val state = InitBeginnerState( None, Map.empty, 1 )
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
        state.initPlayers() shouldBe a [Some[InitPlayerState]]
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
