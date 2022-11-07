package com.aimit.htwg.catan

import com.aimit.htwg.catan.model.impl.game.ClassicGameImpl
import com.aimit.htwg.catan.model.impl.gamefield.ClassicGameFieldImpl
import com.aimit.htwg.catan.model.impl.player.ClassicPlayerImpl
import com.aimit.htwg.catan.model.impl.turn.ClassicTurnImpl
import com.aimit.htwg.catan.model.{ FileIO, Game, GameField, Placement, Player, PlayerFactory, Turn }
import com.google.inject.assistedinject.FactoryModuleBuilder
import com.google.inject.name.Names

/**
 * @author Vincent76
 */
object ClassicCatanModule extends CatanModuleImpl( "Classic" ) {
  /*def apply( test:Boolean = false,
             fileIO:FileIO = JsonFileIO,
             availablePlacements:Set[Placement] = CatanModule.availablePlacements
           ):ClassicCatanModule =
    new ClassicCatanModule( test, fileIO, availablePlacements )*/

  override def _create( test:Boolean, fileIO:FileIO, availablePlacements:Set[Placement] ):CatanModule =
    ClassicCatanModule.create( test, fileIO, availablePlacements )
}

class ClassicCatanModule( test:Boolean,
                          fileIO:FileIO,
                          availablePlacements:Set[Placement]
                        ) extends CatanModule( test, fileIO, availablePlacements ) {

  override def bindAll( seed:Int ):Unit = {
    bind[Int].annotatedWith( Names.named( "seed" ) ).toInstance( seed )
    bind[Game].to[ClassicGameImpl]
    bind[Turn].to[ClassicTurnImpl]
    bind[GameField].toInstance( ClassicGameFieldImpl( seed ) )
    val playerClass = classOf[ClassicPlayerImpl]
    bind[String].annotatedWith( Names.named( "playerFactoryClass" ) ).toInstance( playerClass.getSimpleName )
    install( new FactoryModuleBuilder()
      .implement( classOf[Player], playerClass )
      .build( classOf[PlayerFactory] )
    )
  }
}
