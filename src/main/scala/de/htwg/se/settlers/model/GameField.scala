package de.htwg.se.settlers.model

import scala.collection.immutable.HashMap
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
case class GameField(
                      areas:HashMap[Hex, Area],
                    ) {

}

case class Hex( r:Int, c:Int )

object Coords {
  val all:Vector[Vector[Option[Hex]]] = ( 0 to 6 ).red( Vector(), ( res:Vector[Vector[Option[Hex]]], i:Int ) => {
    res :+ ( 0 to 6 ).red( Vector(), ( sub:Vector[Option[Hex]], j:Int ) => {
      sub :+ ( if ( ( ( i + 2 ) * ( j + 2 ) ).check( v => v >= 10 && v <= 42 ) )
        Some( Hex( i, j ) )
      else
        Option.empty )
    } )
  } )
}