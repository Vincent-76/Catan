package de.htwg.se.catan.util

import de.htwg.se.catan.model.{ Player, PlayerColor, PlayerID }

trait PlayerFactory {
  def create( pID:PlayerID, color:PlayerColor, name:String ):Player
}
