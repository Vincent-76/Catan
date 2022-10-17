package com.aimit.htwg.catan.view.tui

import com.aimit.htwg.catan.view.tui.impl.gamefield.ClassicGameFieldDisplayImpl
import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.model.impl.gamefield.ClassicGameFieldImpl

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
