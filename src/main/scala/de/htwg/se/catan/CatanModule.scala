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
import de.htwg.se.catan.model.impl.fileio.{ XMLFileIO, XMLParseError }
import net.codingwell.scalaguice.ScalaModule

import scala.util.Random
import scala.xml.Node

object CatanModule {

  def gameFromXML( node:Node ):Game = node.label match {
    case "ClassicGameImpl" => ClassicGameImpl.fromXML( node )
    case e => throw XMLParseError( expected = "Game", got = e )
  }

  def gameFieldFromXML( node:Node ):GameField = node.label match {
    case "ClassicGameFieldImpl" => ClassicGameFieldImpl.fromXML( node )
    case e => throw XMLParseError( expected = "GameField", got = e )
  }

  def turnFromXML( node:Node ):Turn = node.label match {
    case "ClassicTurnImpl" => ClassicTurnImpl.fromXML( node )
    case e => throw XMLParseError( expected = "Turn", got = e )
  }

  def playerFromXML( node:Node ):Player = node.label match {
    case "ClassicPlayerImpl" => ClassicPlayerImpl.fromXML( node )
    case e => throw XMLParseError( expected = "Player", got = e )
  }

  def playerFactoryFromXML( playerFactoryClass:String ):PlayerFactory = playerFactoryClass match {
    case "ClassicPlayerImpl" => ( pID:PlayerID, color:PlayerColor, name:String ) => ClassicPlayerImpl( pID, color, name )
    case e => throw XMLParseError( expected = "PlayerFactoryClass", got = e )
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
    bind[FileIO].to[XMLFileIO]
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