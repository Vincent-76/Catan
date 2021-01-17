package de.htwg.se.settlers.model

import de.htwg.se.settlers.model.Cards.ResourceCards

/**
 * @author Vincent76;
 */
object StructurePlacement {
  def get:List[StructurePlacement] = List(
    Road,
    Settlement,
    City
  )

  def of( s:String ):Option[StructurePlacement] = get.find( _.s.toLowerCase == s.toLowerCase )
}

sealed abstract class Placement( val s:String )

sealed abstract class StructurePlacement( s:String, val available:Int, val resources:ResourceCards ) extends Placement( s )

sealed abstract class VertexPlacement( s:String, available:Int, resources:ResourceCards ) extends StructurePlacement( s, available, resources )

case object Road extends StructurePlacement( "Road", 15, Map( Wood -> 1, Clay -> 1 ) )

case object Settlement extends VertexPlacement( "Settlement", 5, Map( Wood -> 1, Clay -> 1, Sheep -> 1, Wheat -> 1 ) )

case object City extends VertexPlacement( "City", 4, Map( Wheat -> 2, Ore -> 3 ) )

case object Robber extends Placement( "Robber" )



sealed abstract class Structure( val owner:Int )

sealed abstract class Building( owner:Int ) extends Structure( owner )

case class Road( override val owner:Int ) extends Structure( owner )

case class Settlement( override val owner:Int ) extends Building( owner )

case class City( override val owner:Int ) extends Building( owner )
