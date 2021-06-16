package de.htwg.se.settlers.aview.tui

import de.htwg.se.settlers.aview.tui.impl.gamefield.ClassicGameFieldDisplayImpl
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.model.impl.gamefield.ClassicGameFieldImpl

/**
 * @author Vincent76;
 */

object GameFieldDisplay {
  def get( game:Game, buildableIDs:Option[List[PlacementPoint]] = None ):GameFieldDisplay = game.gameField match {
    case gf:ClassicGameFieldImpl => ClassicGameFieldDisplayImpl( game, gf, buildableIDs )
    case c => throw new NotImplementedError( "GameFieldDisplay[" + c.getClass.getName + "]" )
  }
}

trait GameFieldDisplay {

  def buildGameField:String
}
