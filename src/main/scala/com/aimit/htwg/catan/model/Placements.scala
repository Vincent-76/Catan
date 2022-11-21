package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.model.Card.ResourceCards
import com.aimit.htwg.catan.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import play.api.libs.json._

import scala.collection.immutable.List
import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */

object Placement extends NamedComponent[Placement] {
  CityPlacement.init()
  RoadPlacement.init()
  RobberPlacement.init()
  SettlementPlacement.init()
}

abstract class Placement( title:String ) extends NamedComponentImpl( title ) {
  override def init():Unit = Placement.addImpl( this )

  def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean = false ):List[PlacementPoint]
}



object StructurePlacement extends NamedComponent[StructurePlacement]

abstract class StructurePlacement( title:String,
                                   val available:Int,
                                   val resources:ResourceCards,
                                   val replaces:Option[StructurePlacement] = None
                                 ) extends Placement( title ) {
  StructurePlacement.addImpl( this )

  def build( game:Game, pID:PlayerID, id:Int, anywhere:Boolean = false ):Try[Game] =
    game.player( pID ).getStructure( this ) match {
      case Success( newPlayer ) => doBuild( game.updatePlayer( newPlayer ), pID, id, anywhere )
      case Failure( e ) => Failure( e )
    }

  protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean = false ):Try[Game]
}



object VertexPlacement extends NamedComponent[VertexPlacement]

abstract class VertexPlacement( title:String,
                                available:Int,
                                resources:ResourceCards,
                                replaces:Option[StructurePlacement] = None
                              ) extends StructurePlacement( title, available, resources, replaces ) {

  VertexPlacement.addImpl( this )
}

