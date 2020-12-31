package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */
abstract class Card( val amount:Int )


abstract class ResourceCard extends Card( 19 )

case object Wood extends ResourceCard

case object Clay extends ResourceCard

case object Sheep extends ResourceCard

case object Wheat extends ResourceCard

case object Ore extends ResourceCard


abstract class DevelopmentCard( amount:Int ) extends Card( amount )

case object KnightCard extends DevelopmentCard( 14 )

case object ResearchCard extends DevelopmentCard( 6 )

case object VictoryPointCard extends DevelopmentCard( 5 )
