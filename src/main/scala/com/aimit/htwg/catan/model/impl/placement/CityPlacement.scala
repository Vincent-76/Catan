package com.aimit.htwg.catan.model.impl.placement

import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.util._

import scala.util.{ Failure, Success, Try }

object CityPlacement extends VertexPlacement( "City", 4, Map( Wheat -> 2, Ore -> 3 ), Some( SettlementPlacement ) ) {

  override def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean ):List[PlacementPoint] = {
    game.gameField.vertexList.red( List.empty, ( l:List[Vertex], v:Vertex ) => v.building match {
      case Some( b:Settlement ) if b.owner == pID => l :+ v
      case _ => l
    } )
  }

  override protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean ):Try[Game] = {
    val vertex = game.gameField.findVertex( id )
    if( vertex.isEmpty )
      Failure( NonExistentPlacementPoint( id ) )
    else if( vertex.get.building.isEmpty || !vertex.get.building.get.isInstanceOf[Settlement] )
      Failure( SettlementRequired( id ) )
    else if( vertex.get.building.get.owner != pID )
      Failure( InvalidPlacementPoint( id ) )
    else
      Success( game.setGameField( game.gameField.update( vertex.get.setBuilding( Some( City( pID ) ) ) ) )
        .updatePlayer( game.players( pID ).addVictoryPoint() )
      )
  }
}