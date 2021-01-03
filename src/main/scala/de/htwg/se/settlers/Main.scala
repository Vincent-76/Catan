package de.htwg.se.settlers

import de.htwg.se.settlers.model.Coords.Hexagons
import de.htwg.se.settlers.model.{ Coords, Hex }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
object Main {
  /*def main( args:Array[String] ):Unit = {
    show( Coords.all )
    println()
    println()
    println( Coords.createEdges(Map()).get( (Coords.find( 2, 3 ).get, Coords.find( 2, 4 ).get) ) )
  }

  def show( v:Hexagons ):Unit = {
    v.foreach( r => println( r.red( "", ( s:String, hex:Option[Hex] ) => {
      s + "  " + ( if( hex.isDefined ) hex.get.r + "[" + hex.get.displayID + "]" + hex.get.c else "-[--]-"  )
    } ) )
    )
  }*/
}
