package de.htwg.se.catan.model

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.model.impl.placement.{ CityPlacement, RoadPlacement, SettlementPlacement }

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */


abstract class Placement( val title:String ) {
  def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean = false ):List[PlacementPoint]
}


object StructurePlacement {
  def all:List[StructurePlacement] = List(
    RoadPlacement,
    SettlementPlacement,
    CityPlacement
  )

  def of( s:String ):Option[StructurePlacement] = all.find( _.title.toLowerCase == s.toLowerCase )
}

abstract class StructurePlacement( title:String,
                                   val available:Int,
                                   val resources:ResourceCards,
                                   val replaces:Option[StructurePlacement] = None
                                 ) extends Placement( title ) {

  def build( game:Game, pID:PlayerID, id:Int, anywhere:Boolean = false ):Try[Game] = game.player( pID ).getStructure( this ) match {
    case Success( newPlayer ) => doBuild( game.updatePlayer( newPlayer ), pID, id, anywhere )
    case Failure( e ) => Failure( e )
  }

  protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean = false ):Try[Game]
}

abstract class VertexPlacement( title:String,
                                available:Int,
                                resources:ResourceCards,
                                replaces:Option[StructurePlacement] = None
                              ) extends StructurePlacement( title, available, resources, replaces )

