package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID

import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */

sealed abstract class Structure( val owner:PlayerID )

sealed abstract class Building( owner:PlayerID ) extends Structure( owner )

case class Road( override val owner:PlayerID ) extends Structure( owner )

case class Settlement( override val owner:PlayerID ) extends Building( owner )

case class City( override val owner:PlayerID ) extends Building( owner )


sealed abstract class Placement( val s:String )


object StructurePlacement {
  def get:List[StructurePlacement] = List(
    Road,
    Settlement,
    City
  )

  def of( s:String ):Option[StructurePlacement] = get.find( _.s.toLowerCase == s.toLowerCase )
}

sealed abstract class StructurePlacement( s:String,
                                          val available:Int,
                                          val resources:ResourceCards,
                                          val replaces:Option[StructurePlacement] = Option.empty
                                        ) extends Placement( s ) {

  def build( game:Game, pID:PlayerID, id:Int, anywhere:Boolean = false ):Try[Game] = game.player( pID ).getStructure( this ) match {
    case Success( newPlayer ) => doBuild( game.updatePlayer( newPlayer ), pID, id, anywhere )
    case Failure( e ) => Failure( e )
  }

  protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean = false ):Try[Game]
}

sealed abstract class VertexPlacement( s:String,
                                       available:Int,
                                       resources:ResourceCards,
                                       replaces:Option[StructurePlacement] = Option.empty
                                     ) extends StructurePlacement( s, available, resources, replaces )

case object Road extends StructurePlacement( "Road", 15, Map( Wood -> 1, Clay -> 1 ) ) {

  override protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean ):Try[Game] = {
    val edge = game.gameField.findEdge( id )
    if ( edge.isEmpty )
      Failure( NonExistentPlacementPoint( id ) )
    else if ( edge.get.road.isDefined )
      Failure( PlacementPointNotEmpty( id ) )
    else if ( !game.roadBuildable( edge.get, pID ) )
      Failure( NoConnectedStructures( id ) )
    else {
      val length = game.roadLength( pID, edge.get )
      val newBonusCards =
        if ( length >= LongestRoadCard.minimumRoads &&
          ( game.bonusCards( LongestRoadCard ).isEmpty || length > game.bonusCards( LongestRoadCard ).get._2 )
        )
          game.bonusCards.updated( LongestRoadCard, Some( pID, length ) )
        else game.bonusCards
      Success( game.copy(
        gameField = game.gameField.update( edge.get.setRoad( Some( Road( pID ) ) ) ),
        bonusCards = newBonusCards
      ) )
    }
  }
}

case object Settlement extends VertexPlacement( "Settlement", 5, Map( Wood -> 1, Clay -> 1, Sheep -> 1, Wheat -> 1 ) ) {

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

case object Robber extends Placement( "Robber" )