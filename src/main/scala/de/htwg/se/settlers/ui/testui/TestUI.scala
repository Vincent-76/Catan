package de.htwg.se.settlers.ui.testui

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.{ Game, Info, State, StructurePlacement }
import de.htwg.se.settlers.model.state.{ ActionState, BuildInitRoadState, BuildInitSettlementState, BuildState, DevRoadBuildingState, DiceState, DropHandCardsState, InitBeginnerState, InitPlayerState, InitState, MonopolyState, NextPlayerState, PlayerTradeEndState, PlayerTradeState, RobberPlaceState, RobberStealState, YearOfPlentyState }
import de.htwg.se.settlers.ui.UI

/**
 * @author Vincent76;
 */
class TestUI( controller:Controller ) extends UI {

  override def start( ):Unit = {}

  override def show( ):Unit = {}

  override def onInfo( info:Info ):Unit = {}

  override def onError( t:Throwable ):Unit = {}

  override def getInitState:InitState =
    new InitState( controller ) {}

  override def getInitPlayerState:InitPlayerState =
    new InitPlayerState( controller ) {}

  override def getInitBeginnerState( diceValues:Map[Game.PlayerID, Int], counter:Int ):InitBeginnerState =
    new InitBeginnerState( diceValues, counter, controller ) {}

  override def getBuildInitSettlementState:BuildInitSettlementState =
    new BuildInitSettlementState( controller ) {}

  override def getBuildInitRoadState( vID:Int ):BuildInitRoadState =
    new BuildInitRoadState( vID, controller ) {}

  override def getNextPlayerState:NextPlayerState =
    new NextPlayerState( controller ) {}

  override def getDiceState( dices:(Int, Int) ):DiceState =
    new DiceState( dices, controller ) {}

  override def getDropHandCardsState( pID:Game.PlayerID, dropped:List[Game.PlayerID] ):DropHandCardsState =
    new DropHandCardsState( pID, dropped, controller ) {}

  override def getRobberPlaceState( nextState:State ):RobberPlaceState =
    new RobberPlaceState( nextState, controller ) {}

  override def getRobberStealState( nextState:State ):RobberStealState =
    new RobberStealState( nextState, controller ) {}

  override def getActionState:ActionState =
    new ActionState( controller ) {}

  override def getBuildState( structure:StructurePlacement ):BuildState =
    new BuildState( structure, controller ) {}

  override def getPlayerTradeState( pID:Game.PlayerID, give:ResourceCards, get:ResourceCards, decisions:Map[Game.PlayerID, Boolean] ):PlayerTradeState =
    new PlayerTradeState( pID, give, get, decisions, controller ) {}

  override def getPlayerTradeEndState( give:ResourceCards, get:ResourceCards, decisions:Map[Game.PlayerID, Boolean] ):PlayerTradeEndState =
    new PlayerTradeEndState( give, get, decisions, controller ) {}

  override def getYearOfPlentyState( nextState:State ):YearOfPlentyState =
    new YearOfPlentyState( nextState, controller ) {}

  override def getDevRoadBuildingState( nextState:State, roads:Int ):DevRoadBuildingState =
    new DevRoadBuildingState( nextState, roads, controller ) {}

  override def getMonopolyState( nextState:State ):MonopolyState =
    new MonopolyState( nextState, controller ) {}
}
