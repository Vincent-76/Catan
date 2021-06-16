package de.htwg.se.settlers

import de.htwg.se.settlers.model.{ Player, PlayerColor, PlayerID }

trait PlayerFactory {
  def create( pID:PlayerID, color:PlayerColor, name:String ):Player
}
