package de.htwg.se.catan.model

import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.impl.placement.{ CityPlacement, RoadPlacement, RobberPlacement, SettlementPlacement }
import de.htwg.se.catan.util.^=
import play.api.libs.json._

import scala.collection.immutable.List
import scala.util.{ Failure, Success, Try }

/**
 * @author Vincent76;
 */

object Placement extends ObjectComponent[Placement]:
  given placementWrites:Writes[Placement] = ( o:Placement ) => Json.toJson( o.title )
  given placementReads:Reads[Placement] = ( json:JsValue ) => JsSuccess( of( json.as[String] ).get )

  CityPlacement.init()
  RoadPlacement.init()
  RobberPlacement.init()
  SettlementPlacement.init()

  def of( title:String ):Option[Placement] = impls.find( _.title ^= title )


abstract class Placement( val title:String ) extends ComponentImpl:
  override def init():Unit = Placement.addImpl( this )

  def getBuildablePoints( game:Game, pID:PlayerID, any:Boolean = false ):List[PlacementPoint]




object StructurePlacement extends ObjectComponent[StructurePlacement]:
  given structurePlacementWrites:Writes[StructurePlacement] = ( o:StructurePlacement ) => Json.toJson( o.title )
  given structurePlacementReads:Reads[StructurePlacement] = ( json:JsValue ) => JsSuccess( of( json.as[String] ).get )

  def of( s:String ):Option[StructurePlacement] = impls.find( _.title ^= s )


abstract class StructurePlacement( title:String,
                                   val available:Int,
                                   val resources:ResourceCards,
                                   val replaces:Option[StructurePlacement] = None
                                 ) extends Placement( title ):
  StructurePlacement.addImpl( this )

  def build( game:Game, pID:PlayerID, id:Int, anywhere:Boolean = false ):Try[Game] =
    game.player( pID ).getStructure( this ) match
      case Success( newPlayer ) => doBuild( game.updatePlayer( newPlayer ), pID, id, anywhere )
      case Failure( e ) => Failure( e )

  protected def doBuild( game:Game, pID:PlayerID, id:Int, anywhere:Boolean = false ):Try[Game]



object VertexPlacement extends ObjectComponent[VertexPlacement]:
  given vertexPlacementWrites:Writes[VertexPlacement] = ( o:VertexPlacement ) => Json.toJson( o.title )
  given vertexPlacementReads:Reads[VertexPlacement] = ( json:JsValue ) => JsSuccess( of( json.as[String] ).get )

  def of( s:String ):Option[VertexPlacement] = impls.find( _.title ^= s )


abstract class VertexPlacement( title:String,
                                available:Int,
                                resources:ResourceCards,
                                replaces:Option[StructurePlacement] = None
                              ) extends StructurePlacement( title, available, resources, replaces ):

  VertexPlacement.addImpl( this )

