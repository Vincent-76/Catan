package de.htwg.se.settlers

import com.google.inject.assistedinject.FactoryModuleBuilder
import com.google.inject.{ AbstractModule, Module, PrivateModule }
import com.google.inject.internal.ProviderInternalFactory
import com.google.inject.name.Names
import com.google.inject.spi.ProviderLookup
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.controller.controllerBaseImpl.ClassicControllerImpl
import de.htwg.se.settlers.model.{ Game, GameField, Placement, Player, Turn }
import de.htwg.se.settlers.model.impl.game.ClassicGameImpl
import de.htwg.se.settlers.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.settlers.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import de.htwg.se.settlers.model.impl.player.ClassicPlayerImpl
import de.htwg.se.settlers.model.impl.turn.ClassicTurnImpl
import net.codingwell.scalaguice.ScalaModule

import scala.util.Random

class CatanModule( val test:Boolean = false ) extends AbstractModule with ScalaModule {

  val availablePlacements:List[Placement] = List(
    RobberPlacement,
    RoadPlacement,
    SettlementPlacement,
    CityPlacement,
  )

  override def configure( ):Unit = {
    bind[Controller].to[ClassicControllerImpl]
    bind[Game].to[ClassicGameImpl]
    bind[Turn].to[ClassicTurnImpl]
    val seed = if( test ) 1 else Random.nextInt( Int.MaxValue / 1000 )
    bind[Int].annotatedWith( Names.named( "seed" ) ).toInstance( seed )
    bind[GameField].toInstance( ClassicGameFieldImpl( seed ) )
    install( new FactoryModuleBuilder()
      .implement( classOf[Player], classOf[ClassicPlayerImpl] )
    .build( classOf[PlayerFactory] ) )
  }
}