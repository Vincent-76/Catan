package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */
case class Game( val turn:Int ) {


  def dropCards( iterable: Iterable[Card] ):Game = this
}
