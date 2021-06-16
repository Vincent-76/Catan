package de.htwg.se.catan.model.impl.placement

import de.htwg.se.catan.model._
import de.htwg.se.catan.util._

import scala.util.{ Failure, Success, Try }

object RoadPlacement extends StructurePlacement( "Road", 15, Map( Wood -> 1, Clay -> 1 ) ) {

  override def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean ):List[PlacementPoint] = {
    game.gameField.edgeList.red( (List.empty, List.empty), ( d:(List[Edge], List[Int]), edge:Edge ) => {
      if( !d._2.contains( edge.id ) && edge.road.isDefined && edge.road.get.owner == pID ) {
        val nd = game.gameField.adjacentEdges( edge ).filter( e => !d._2.contains( e.id ) ).red( d, ( d:(List[Edge], List[Int]), e:Edge ) => {
          if( e.road.isEmpty && (e.h1.isLand || e.h2.isLand) )
            (d._1 :+ e, d._2 :+ e.id)
          else if( e.road.useOrElse( road => road.owner != pID, true ) )
            (d._1, d._2 :+ e.id)
          else d
        } )
        (nd._1, nd._2 :+ edge.id)
      } else d
    } )._1
  }

  override protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean ):Try[Game] = {
    val edge = game.gameField.findEdge( id )
    if( edge.isEmpty )
      Failure( NonExistentPlacementPoint( id ) )
    else if( edge.get.road.isDefined )
      Failure( PlacementPointNotEmpty( id ) )
    else if( !game.roadBuildable( edge.get, pID ) )
      Failure( NoConnectedStructures( id ) )
    else {
      val newGame = game.setGameField( game.gameField.update( edge.get.setRoad( Some( Road( pID ) ) ) ) )
      val length = newGame.getLongestRoadLength( pID )
      val longestRoadCardValue =
        if( length >= LongestRoadCard.minimumRoads &&
          (newGame.bonusCard( LongestRoadCard ).isEmpty || length > newGame.bonusCard( LongestRoadCard ).get._2)
        )
          Some( pID, length )
        else newGame.bonusCard( LongestRoadCard )
      Success( newGame.setBonusCard( LongestRoadCard, longestRoadCardValue )
      )
    }
  }

}
