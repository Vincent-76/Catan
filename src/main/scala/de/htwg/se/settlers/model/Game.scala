package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */
case class Game( phase:Phase = InitPhase ) {

  def setPhase( phase:Phase ):Game = copy( phase = phase )

  def dropCards( iterable: Iterable[Card] ):Game = this
}
