package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */
sealed trait Phase

case object InitPhase extends Phase

case object PlayerPhase extends Phase
