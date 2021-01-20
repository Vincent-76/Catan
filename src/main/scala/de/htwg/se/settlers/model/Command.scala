package de.htwg.se.settlers.model

import de.htwg.se.settlers.controller.Controller

import scala.util.Try

/**
 * @author Vincent76;
 */
trait Command {

  def doStep( controller:Controller, game:Game ):Try[(Game, Option[Info])]

  def undoStep( game:Game ):Game
}
