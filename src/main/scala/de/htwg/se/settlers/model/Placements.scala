package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.ClassicGameField.Row
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.util._

import scala.util.{Failure, Success, Try}

/**
 * @author Vincent76;
 */

sealed abstract class Structure( val owner:PlayerID )

sealed abstract class Building( owner:PlayerID ) extends Structure( owner )

case class Road( override val owner:PlayerID ) extends Structure( owner )

case class Settlement( override val owner:PlayerID ) extends Building( owner )

case class City( override val owner:PlayerID ) extends Building( owner )


sealed abstract class Placement( val title:String ) {
  def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean = false ):List[PlacementPoint]
}


object StructurePlacement {
  def get:List[StructurePlacement] = List(
    Road,
    Settlement,
    City
  )

  def of( s:String ):Option[StructurePlacement] = get.find( _.title.toLowerCase == s.toLowerCase )
}

sealed abstract class StructurePlacement( title:String,
                                          val available:Int,
                                          val resources:ResourceCards,
                                          val replaces:Option[StructurePlacement] = Option.empty
                                        ) extends Placement( title ) {

  def build( game:Game, pID:PlayerID, id:Int, anywhere:Boolean = false ):Try[Game] = game.player( pID ).getStructure( this ) match {
    case Success( newPlayer ) => doBuild( game.updatePlayer( newPlayer ), pID, id, anywhere )
    case Failure( e ) => Failure( e )
  }

  protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean = false ):Try[Game]
}

sealed abstract class VertexPlacement( title:String,
                                       available:Int,
                                       resources:ResourceCards,
                                       replaces:Option[StructurePlacement] = Option.empty
                                     ) extends StructurePlacement( title, available, resources, replaces )

case object Road extends StructurePlacement( "Road", 15, Map( Wood -> 1, Clay -> 1 ) ) {

  override def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean ):List[PlacementPoint] = {
    game.gameField.edges.values.red( (List.empty, List.empty), ( d:(List[Edge], List[Int]), edge:Edge ) => {
      if ( !d._2.contains( edge.id ) && edge.road.isDefined && edge.road.get.owner == pID ) {
        val nd = game.gameField.adjacentEdges( edge ).filter( e => !d._2.contains( e.id ) ).red( d, ( d:(List[Edge], List[Int]), e:Edge ) => {
          if( e.road.isEmpty && ( e.h1.isLand || e.h2.isLand ) )
            (d._1 :+ e, d._2 :+ e.id)
          else if ( e.road.useOrElse( road => road.owner != pID, true ) )
            (d._1, d._2 :+ e.id)
          else d
        } )
        (nd._1, nd._2 :+ edge.id)
      } else d
    } )._1
  }

  override protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean ):Try[Game] = {
    val edge = game.gameField.findEdge( id )
    if ( edge.isEmpty )
      Failure( NonExistentPlacementPoint( id ) )
    else if ( edge.get.road.isDefined )
      Failure( PlacementPointNotEmpty( id ) )
    else if ( !game.roadBuildable( edge.get, pID ) )
      Failure( NoConnectedStructures( id ) )
    else {
      val newEdge = edge.get.setRoad( Some( Road( pID ) ) )
      val length = game.getRoadLength( pID, newEdge )
      val newBonusCards =
        if ( length >= LongestRoadCard.minimumRoads &&
          ( game.bonusCards( LongestRoadCard ).isEmpty || length > game.bonusCards( LongestRoadCard ).get._2 )
        )
          game.bonusCards.updated( LongestRoadCard, Some( pID, length ) )
        else game.bonusCards
      Success( game.copy(
        gameField = game.gameField.update( newEdge ),
        bonusCards = newBonusCards
      ) )
    }
  }

}

case object Settlement extends VertexPlacement( "Settlement", 5, Map( Wood -> 1, Clay -> 1, Sheep -> 1, Wheat -> 1 ) ) {

  override def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean ):List[PlacementPoint] = {
    if ( any ) {
      game.gameField.vertices.values.red( List.empty, ( l:List[Vertex], v:Vertex ) => {
        if ( v.building.isEmpty && game.noBuildingInRange( v ) )
          l :+ v
        else l
      } )
    } else {
      game.gameField.edges.values.red( (List.empty, List.empty), ( d:(List[Vertex], List[Int]), e:Edge ) => {
        if ( e.road.isDefined && e.road.get.owner == pID )
          game.gameField.adjacentVertices( e ).filter( v => !d._2.contains( v.id ) ).red( d, ( d:(List[Vertex], List[Int]), v:Vertex ) => {
            if ( v.building.isEmpty && game.noBuildingInRange( v ) )
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
    if ( vertex.isEmpty )
      Failure( NonExistentPlacementPoint( id ) )
    else if ( vertex.get.building.isDefined )
      Failure( PlacementPointNotEmpty( id ) )
    else if ( !anywhere && !game.playerHasAdjacentEdge( pID, game.gameField.adjacentEdges( vertex.get ) ) )
      Failure( NoConnectedStructures( id ) )
    else if ( !game.noBuildingInRange( vertex.get ) )
      Failure( TooCloseToBuilding( id ) )
    else
      Success( game.copy(
        gameField = game.gameField.update( vertex.get.setBuilding( Some( Settlement( pID ) ) ) ),
        players = game.players.updated( pID, game.players( pID ).addVictoryPoint() )
      ) )
  }
}

case object City extends VertexPlacement( "City", 4, Map( Wheat -> 2, Ore -> 3 ), Some( Settlement ) ) {

  override def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean ):List[PlacementPoint] = {
    game.gameField.vertices.values.red( List.empty, ( l:List[Vertex], v:Vertex ) => v.building match {
      case Some( b:Settlement ) if b.owner == pID => l :+ v
      case _ => l
    } )
  }

  override protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean ):Try[Game] = {
    val vertex = game.gameField.findVertex( id )
    if ( vertex.isEmpty )
      Failure( NonExistentPlacementPoint( id ) )
    else if ( vertex.get.building.isEmpty || !vertex.get.building.get.isInstanceOf[Settlement] )
      Failure( SettlementRequired( id ) )
    else if ( vertex.get.building.get.owner != pID )
      Failure( InvalidPlacementPoint( id ) )
    else
      Success( game.copy(
        gameField = game.gameField.update( vertex.get.setBuilding( Some( City( pID ) ) ) ),
        players = game.players.updated( pID, game.players( pID ).addVictoryPoint() )
      ) )
  }
}

case object Robber extends Placement( "Robber" ) {

  override def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean ):List[PlacementPoint] = {
    game.gameField.hexagons.red( List.empty, ( l:List[Hex], row:Row[Hex] ) => row.red( l, ( l:List[Hex], hex:Option[Hex] ) => {
      if ( hex.isDefined && hex.get != game.gameField.robber && hex.get.area.isInstanceOf[LandArea] )
        l :+ hex.get
      else l
    } ) )
  }
}