package de.htwg.se.settlers.ui

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.state._
import de.htwg.se.settlers.model.{ Info, State, StructurePlacement }
import de.htwg.se.settlers.ui.tui.TUI

/**
 * @author Vincent76;
 */
object UI {
  def get( s:String, controller:Controller ):UI = s.toLowerCase() match {
    case "gui" => ???
    case _ => new TUI( controller )
  }
}

trait UI {

  def start():Unit

  def show():Unit

  def onInfo( info:Info ):Unit

  def onError( t:Throwable ):Unit

  def getInitState:InitState

  def getInitPlayerState:InitPlayerState

  def getInitBeginnerState( diceValues:Map[PlayerID, Int] = Map.empty, counter:Int = 1 ):InitBeginnerState

  def getBuildInitSettlementState:BuildInitSettlementState

  def getBuildInitRoadState( vID:Int ):BuildInitRoadState

  def getNextPlayerState:NextPlayerState

  def getDiceState( dices:(Int, Int) ):DiceState

  def getDropHandCardsState( pID:PlayerID, dropped:List[PlayerID] = List.empty ):DropHandCardsState

  def getRobberPlaceState( nextState:State ):RobberPlaceState

  def getRobberStealState( nextState:State ):RobberStealState


  def getActionState:ActionState

  def getBuildState( structure:StructurePlacement ):BuildState

  def getPlayerTradeState( pID:PlayerID, give:ResourceCards, get:ResourceCards, decisions:Map[PlayerID, Boolean] = Map.empty ):PlayerTradeState

  def getPlayerTradeEndState( give:ResourceCards, get:ResourceCards, decisions:Map[PlayerID, Boolean] ):PlayerTradeEndState


  def getYearOfPlentyState( nextState:State ):YearOfPlentyState

  def getDevRoadBuildingState( nextState:State, roads:Int = 0 ):DevRoadBuildingState

  def getMonopolyState( nextState:State ):MonopolyState

}
