package de.htwg.se.settlers.aview.tui

import de.htwg.se.settlers.model._

/**
 * @author Vincent76;
 */

sealed abstract class EdgeDir( val symbol:String )

case object SouthWest extends EdgeDir( "\\" )

case object SouthEast extends EdgeDir( "/" )

case object East extends EdgeDir( "|" )

case object NorthEast extends EdgeDir( "\\" )

case object NorthWest extends EdgeDir( "/" )

case object West extends EdgeDir( "|" )

trait GameDisplay[T <: GameField] {

  def buildGameField:String

  def buildPlayerDisplay( turnPlayer:Option[PlayerID] = None ):String
}
