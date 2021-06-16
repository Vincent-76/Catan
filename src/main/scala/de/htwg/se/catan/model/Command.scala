package de.htwg.se.catan.model

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */

trait Command {

  type CommandSuccess = (Game, Option[Info])

  def success( game:Game, info:Option[Info] = None ):Success[CommandSuccess] = Success( (game, info) )

  def doStep( game:Game ):Try[CommandSuccess]

  def undoStep( game:Game ):Game
}
