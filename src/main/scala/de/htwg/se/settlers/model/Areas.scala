package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */

abstract class Area( val f:FieldType )

case class WaterArea( port:Option[Port] = None ) extends Area( Water )

case class Port( specific:Option[Resource] = None )


abstract class LandArea( override val f:FieldType ) extends Area( f )

case object DesertArea extends LandArea( Desert )

case class ResourceArea( resource:Resource, number:DiceValue ) extends LandArea( resource )
