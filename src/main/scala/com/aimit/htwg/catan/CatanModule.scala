package com.aimit.htwg.catan

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import com.google.inject.name.Names
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.model.commands._
import com.aimit.htwg.catan.model.impl.fileio.{ JsonFileIO, XMLFileIO }
import com.aimit.htwg.catan.model.impl.game.ClassicGameImpl
import com.aimit.htwg.catan.model.impl.gamefield.ClassicGameFieldImpl
import com.aimit.htwg.catan.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import com.aimit.htwg.catan.model.impl.player.ClassicPlayerImpl
import com.aimit.htwg.catan.model.impl.turn.ClassicTurnImpl
import com.aimit.htwg.catan.model.state._
import net.codingwell.scalaguice.ScalaModule

import scala.util.Random

object CatanModule {
  val savegamePath:String = "savegames"

  val availablePlacements:List[Placement] = List(
    RobberPlacement,
    RoadPlacement,
    SettlementPlacement,
    CityPlacement,
  )

  def playerFactoryFromString( playerFactoryClass:String ):Option[PlayerFactory] = playerFactoryClass match {
    case "ClassicPlayerImpl" => Some( ( pID:PlayerID, color:PlayerColor, name:String ) => ClassicPlayerImpl( pID, color, name ) )
    case _ => None
  }

  def init():Unit = {
    ClassicGameImpl.init()
    ClassicGameFieldImpl.init()
    ClassicPlayerImpl.init()
    ClassicTurnImpl.init()

    ActionState.init()
    BuildInitRoadState.init()
    BuildInitSettlementState.init()
    BuildState.init()
    DevRoadBuildingState.init()
    DiceState.init()
    DropHandCardsState.init()
    InitBeginnerState.init()
    InitPlayerState.init()
    InitState.init()
    MonopolyState.init()
    NextPlayerState.init()
    PlayerTradeEndState.init()
    PlayerTradeState.init()
    RobberPlaceState.init()
    RobberStealState.init()
    YearOfPlentyState.init()

    AbortPlayerTradeCommand.init()
    AddPlayerCommand.init()
    BankTradeCommand.init()
    BuildCommand.init()
    BuildInitRoadCommand.init()
    BuildInitSettlementCommand.init()
    BuyDevCardCommand.init()
    ChangeStateCommand.init()
    DevBuildRoadCommand.init()
    DiceOutBeginnerCommand.init()
    DropHandCardsCommand.init()
    EndTurnCommand.init()
    InitGameCommand.init()
    MonopolyCommand.init()
    PlaceRobberCommand.init()
    PlayerTradeCommand.init()
    PlayerTradeDecisionCommand.init()
    RobberStealCommand.init()
    RollDicesCommand.init()
    SetBeginnerCommand.init()
    SetBuildStateCommand.init()
    SetInitBeginnerStateCommand.init()
    SetPlayerTradeStateCommand.init()
    UseDevCardCommand.init()
    YearOfPlentyCommand.init()

    Structure.init()
    Area.init()

    XMLFileIO.init()
    JsonFileIO.init()
  }
}

abstract class CatanModule( availablePlacements:List[Placement] ) extends AbstractModule with ScalaModule {

  def bindFileIO():Unit
  def bindGame():Unit
  def bindTurn():Unit
  def bindGameField():Unit
  def bindPlayer():Unit
  def bindOther():Unit

  override def configure( ):Unit = {
    bindFileIO()
    bindGame()
    bindTurn()
    bindGameField()
    bindPlayer()
    bindOther()
    bind[List[Placement]].annotatedWith( Names.named( "availablePlacements" ) ).toInstance( availablePlacements )
  }
}