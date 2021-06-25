package de.htwg.se.catan

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import com.google.inject.name.Names
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.controller.controllerBaseImpl.ClassicControllerImpl
import de.htwg.se.catan.model._
import de.htwg.se.catan.model.impl.fileio.JsonFileIO
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import de.htwg.se.catan.model.impl.turn.ClassicTurnImpl
import de.htwg.se.catan.model.state._
import net.codingwell.scalaguice.ScalaModule

import scala.util.Random

object CatanModule {
  val savegamePath:String = "savegames"

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

    Structure.init()
    Area.init()
  }
}

class CatanModule( val test:Boolean = false ) extends AbstractModule with ScalaModule {

  val availablePlacements:List[Placement] = List(
    RobberPlacement,
    RoadPlacement,
    SettlementPlacement,
    CityPlacement,
  )

  override def configure( ):Unit = {
    bind[Controller].to[ClassicControllerImpl]
    bind[FileIO].to[JsonFileIO]
    bind[Game].to[ClassicGameImpl]
    bind[Turn].to[ClassicTurnImpl]
    val seed = if( test ) 1 else Random.nextInt( Int.MaxValue / 1000 )
    bind[Int].annotatedWith( Names.named( "seed" ) ).toInstance( seed )
    bind[GameField].toInstance( ClassicGameFieldImpl( seed ) )
    val playerClass = classOf[ClassicPlayerImpl]
    bind[String].annotatedWith( Names.named( "playerFactoryClass" ) ).toInstance( playerClass.getSimpleName )
    install( new FactoryModuleBuilder()
        .implement( classOf[Player], playerClass )
        .build( classOf[PlayerFactory] )
    )
    bind[List[Placement]].annotatedWith( Names.named( "availablePlacements" ) ).toInstance( availablePlacements )
  }
}