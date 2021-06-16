package de.htwg.se.catan.model.impl.placement

import de.htwg.se.catan.model._
import de.htwg.se.catan.util._

import scala.util.{ Failure, Success, Try }

object SettlementPlacement extends VertexPlacement( "Settlement", 5, Map( Wood -> 1, Clay -> 1, Sheep -> 1, Wheat -> 1 ) ) {

  override def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean ):List[PlacementPoint] = {
    if( any ) {
      game.gameField.vertexList.red( List.empty, ( l:List[Vertex], v:Vertex ) => {
        if( v.building.isEmpty && game.noBuildingInRange( v ) )
          l :+ v
        else l
      } )
    } else {
      game.gameField.edgeList.red( (List.empty, List.empty), ( d:(List[Vertex], List[Int]), e:Edge ) => {
        if( e.road.isDefined && e.road.get.owner == pID )
          game.gameField.adjacentVertices( e ).filter( v => !d._2.contains( v.id ) ).red( d, ( d:(List[Vertex], List[Int]), v:Vertex ) => {
            if( v.building.isEmpty && game.noBuildingInRange( v ) )
              (d._1 :+ v, d._2 :+ v.id)
            else
              (d._1, d._2 :+ v.id)
          } )
        else d
      } )._1
    }
  }

  override protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean ):Try[Game] = {
    val vertex = game.gameField.findVertex( id )
    if( vertex.isEmpty )
      Failure( NonExistentPlacementPoint( id ) )
    else if( vertex.get.building.isDefined )
      Failure( PlacementPointNotEmpty( id ) )
    else if( !anywhere && !game.playerHasAdjacentEdge( pID, game.gameField.adjacentEdges( vertex.get ) ) )
      Failure( NoConnectedStructures( id ) )
    else if( !game.noBuildingInRange( vertex.get ) )
      Failure( TooCloseToBuilding( id ) )
    else
      Success( game.setGameField( game.gameField.update( vertex.get.setBuilding( Some( Settlement( pID ) ) ) ) )
        .updatePlayer( game.players( pID ).addVictoryPoint() )
      )
  }
}
