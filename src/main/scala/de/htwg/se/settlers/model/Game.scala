package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */
case class Game( phase:Phase ) {


  def dropCards( iterable: Iterable[Card] ):Game = this
}
