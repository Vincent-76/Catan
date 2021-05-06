package de.htwg.se.settlers.model

import scala.util.Try

/**
 * @author Vincent76;
 */
trait Command {

  def doStep( game:Game ):Try[(Game, Option[Info])]

  def undoStep( game:Game ):Game
}
