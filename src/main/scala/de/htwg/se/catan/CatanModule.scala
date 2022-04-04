package de.htwg.se.catan

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import com.google.inject.name.Names
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.controller.controllerBaseImpl.ClassicControllerImpl
import de.htwg.se.catan.model.*
import de.htwg.se.catan.model.commands.*
import de.htwg.se.catan.model.impl.fileio.{ JsonFileIO, XMLFileIO }
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import de.htwg.se.catan.model.impl.turn.ClassicTurnImpl
import de.htwg.se.catan.model.state.*
import net.codingwell.scalaguice.ScalaModule

import scala.util.Random

object CatanModule:
  val savegamePath:String = "savegames"

  def playerFactoryFromString( playerFactoryClass:String ):Option[PlayerFactory] = playerFactoryClass match
    case "ClassicPlayerImpl" => Some( ( pID:PlayerID, color:PlayerColor, name:String ) => ClassicPlayerImpl( pID, color, name ) )
    case _ => None

  def init():Unit =
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

class CatanModule( val test:Boolean = false ) extends AbstractModule:

  val availablePlacements:List[Placement] = List(
    RobberPlacement,
    RoadPlacement,
    SettlementPlacement,
    CityPlacement,
  )

  override def configure( ):Unit =
    bind( classOf[Controller] ).to( classOf[ClassicControllerImpl] )
    bind( classOf[FileIO] ).toInstance( JsonFileIO )
    bind( classOf[Game] ).to( classOf[ClassicGameImpl] )
    bind( classOf[Turn] ).to( classOf[ClassicTurnImpl] )
    val seed = if( test ) 1 else Random.nextInt( Int.MaxValue / 1000 )
    bind( classOf[Int] ).annotatedWith( Names.named( "seed" ) ).toInstance( seed )
    bind( classOf[GameField] ).toInstance( ClassicGameFieldImpl( seed ) )
    val playerClass = classOf[ClassicPlayerImpl]
    bind( classOf[String] ).annotatedWith( Names.named( "playerFactoryClass" ) ).toInstance( playerClass.getSimpleName )
    install( new FactoryModuleBuilder()
        .implement( classOf[Player], playerClass )
        .build( classOf[PlayerFactory] )
    )
    bind( classOf[Any] ).annotatedWith( Names.named( "availablePlacements" ) ).toInstance( availablePlacements )
