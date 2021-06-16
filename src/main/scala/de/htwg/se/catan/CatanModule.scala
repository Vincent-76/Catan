package de.htwg.se.catan

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import com.google.inject.name.Names
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.controller.controllerBaseImpl.ClassicControllerImpl
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import de.htwg.se.catan.model.impl.player.ClassicPlayerImpl
import de.htwg.se.catan.model.impl.turn.ClassicTurnImpl
import de.htwg.se.catan.model._
import de.htwg.se.catan.util.PlayerFactory
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
        .build( classOf[PlayerFactory] )
    )
    bind[List[Placement]].annotatedWith( Names.named( "availablePlacements" ) ).toInstance( availablePlacements )
  }
}