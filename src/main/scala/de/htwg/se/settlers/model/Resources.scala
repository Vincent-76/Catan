package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */

sealed abstract class FieldType( val title:String )

case object Water extends FieldType( "Water" )

case object Desert extends FieldType( "Desert" )


object Resources {
  val get:List[Resource] = List(
    Wood,
    Clay,
    Sheep,
    Wheat,
    Ore
  )

  def of( s:String ):Option[Resource] = get.find( _.title.toLowerCase == s.toLowerCase )
}

sealed abstract class Resource( override val title:String ) extends FieldType( title ) {
  override def toString:String = title
}

case object Wood extends Resource( "Wood" )

case object Clay extends Resource( "Clay" )

case object Sheep extends Resource( "Sheep" )

case object Wheat extends Resource( "Wheat" )

case object Ore extends Resource( "Ore" )