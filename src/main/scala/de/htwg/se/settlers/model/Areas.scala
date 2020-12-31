package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */
abstract class Area( r:Int, c:Int )

case class WaterArea( r:Int, c:Int, port:Option[Port] ) extends Area( r, c )

case class Port( specific:Option[ResourceCard] )


abstract class LandArea( r:Int, c:Int, number:Int ) extends Area( r, c )


case class ForestArea( r:Int,
                       c:Int,
                       number:Int ) extends LandArea( r, c, number )

case class HillsArea( r:Int,
                      c:Int,
                      number:Int ) extends LandArea( r, c, number )

case class PastureArea( r:Int,
                        c:Int,
                        number:Int ) extends LandArea( r, c, number )

case class FieldArea( r:Int,
                      c:Int,
                      number:Int ) extends LandArea( r, c, number )

case class MountainArea( r:Int,
                         c:Int,
                         number:Int ) extends LandArea( r, c, number )
