package com.aimit.htwg.catan

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO
import com.aimit.htwg.catan.model.impl.game.ClassicGameImpl
import com.aimit.htwg.catan.model.impl.gamefield.ClassicGameFieldImpl
import com.aimit.htwg.catan.model.impl.player.ClassicPlayerImpl
import com.aimit.htwg.catan.model.impl.turn.ClassicTurnImpl
import com.aimit.htwg.catan.model.{ FileIO, Game, GameField, Placement, Player, PlayerFactory, Turn }
import com.google.inject.assistedinject.FactoryModuleBuilder
import com.google.inject.name.Names

import scala.util.Random

/**
 * @author Vincent76
 */
object ClassicCatanModule {
  def apply( test:Boolean = false, availablePlacements:List[Placement] = CatanModule.availablePlacements ):ClassicCatanModule =
    new ClassicCatanModule( availablePlacements, if( test ) 1 else Random.nextInt( Int.MaxValue / 1000 ) )
}

class ClassicCatanModule( availablePlacements:List[Placement] = CatanModule.availablePlacements,
                          seed:Int
                        ) extends CatanModule( availablePlacements ) {

  override def bindFileIO( ):Unit = bind[FileIO].toInstance( JsonFileIO )

  override def bindGame( ):Unit = bind[Game].to[ClassicGameImpl]

  override def bindTurn( ):Unit = bind[Turn].to[ClassicTurnImpl]

  override def bindGameField( ):Unit = bind[GameField].toInstance( ClassicGameFieldImpl( seed ) )

  override def bindPlayer( ):Unit = {
    val playerClass = classOf[ClassicPlayerImpl]
    bind[String].annotatedWith( Names.named( "playerFactoryClass" ) ).toInstance( playerClass.getSimpleName )
    install( new FactoryModuleBuilder()
      .implement( classOf[Player], playerClass )
      .build( classOf[PlayerFactory] )
    )
  }

  override def bindOther( ):Unit = {
    bind[Int].annotatedWith( Names.named( "seed" ) ).toInstance( seed )
  }
}
