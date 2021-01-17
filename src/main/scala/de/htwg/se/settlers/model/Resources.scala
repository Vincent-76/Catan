package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */

sealed abstract class FieldType( val c:Color, val s:String )

case object Water extends FieldType( ColorBlue, " " )

case object Desert extends FieldType( ColorBlack, "D" )


object Resources {
  val get:List[Resource] = List(
    Wood,
    Clay,
    Sheep,
    Wheat,
    Ore
  )

  def of( s:String ):Option[Resource] = get.find( _.s.toLowerCase == s.toLowerCase )
}

sealed abstract class Resource( override val c:Color, override val s:String ) extends FieldType( c, s )

case object Wood extends Resource( ColorGreen, "Wood" )

case object Clay extends Resource( ColorMagenta, "Clay" )

case object Sheep extends Resource( ColorWhite, "Sheep" )

case object Wheat extends Resource( ColorYellow, "Wheat" )

case object Ore extends Resource( ColorCyan, "Ore" )