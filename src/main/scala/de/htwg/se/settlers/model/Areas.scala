package de.htwg.se.settlers.model

import scala.util.Random

/**
 * @author Vincent76;
 */

object Area {
  def getAvailableAreas:(List[WaterArea], List[WaterArea], List[Option[Resource]], List[Number]) = {
    val portAreas = List(
      WaterArea( Some( Port( Option.empty ) ) ),
      WaterArea( Some( Port( Option.empty ) ) ),
      WaterArea( Some( Port( Option.empty ) ) ),
      WaterArea( Some( Port( Option.empty ) ) ),
      WaterArea( Some( Port( Some( Wood ) ) ) ),
      WaterArea( Some( Port( Some( Clay ) ) ) ),
      WaterArea( Some( Port( Some( Sheep ) ) ) ),
      WaterArea( Some( Port( Some( Wheat ) ) ) ),
      WaterArea( Some( Port( Some( Ore ) ) ) )
    )
    val waterAreas = List(
      WaterArea( Option.empty ),
      WaterArea( Option.empty ),
      WaterArea( Option.empty ),
      WaterArea( Option.empty ),
      WaterArea( Option.empty ),
      WaterArea( Option.empty ),
      WaterArea( Option.empty ),
      WaterArea( Option.empty ),
      WaterArea( Option.empty )
    )
    val landAreas = Random.shuffle( List(
      Option.empty,
      Some( Wood ),
      Some( Wood ),
      Some( Wood ),
      Some( Wood ),
      Some( Clay ),
      Some( Clay ),
      Some( Clay ),
      Some( Sheep ),
      Some( Sheep ),
      Some( Sheep ),
      Some( Sheep ),
      Some( Wheat ),
      Some( Wheat ),
      Some( Wheat ),
      Some( Wheat ),
      Some( Ore ),
      Some( Ore ),
      Some( Ore ),
    ) )
    val numbers = List(
      Six,
      Three,
      Eight,
      Two,
      Four,
      Five,
      Ten,
      Five,
      Nine,
      Six,
      Nine,
      Ten,
      Eleven,
      Three,
      Twelve,
      Eight,
      Four,
      Eleven
    )
    (Random.shuffle( portAreas ), waterAreas, Random.shuffle( landAreas ), numbers)
  }
}

abstract class Area( val f:FieldType )


case class WaterArea( port:Option[Port] = Option.empty ) extends Area( Water )

case class Port( specific:Option[Resource] )


abstract class LandArea( override val f:FieldType ) extends Area( f )

case object DesertArea extends LandArea( Desert )

case class ResourceArea( resource:Resource, number:Number ) extends LandArea( resource )
