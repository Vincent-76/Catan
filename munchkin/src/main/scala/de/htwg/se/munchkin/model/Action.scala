package de.htwg.se.munchkin.model

/**
 * @author Vincent76;
 */
trait Action

trait BadStuffAction {
  def action( pID:Int, game:Game ):Game
}

trait FleeAction {
  def action(  )
}
