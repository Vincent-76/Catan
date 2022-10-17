package com.aimit.htwg.catan.view.gui

import com.aimit.htwg.catan.view.gui.impl.player.ClassicPlayerDisplayImpl
import com.aimit.htwg.catan.model.impl.player.ClassicPlayerImpl
import com.aimit.htwg.catan.model.{ Game, Player }
import scalafx.scene.Node

/**
 * @author Vincent76;
 */

object PlayerDisplay {
  def get( player:Player ):PlayerDisplay = player match {
    case p:ClassicPlayerImpl => ClassicPlayerDisplayImpl( p )
    case c => throw new NotImplementedError( "PlayerDisplay[" + c.getClass.getName + "]" )
  }
}

trait PlayerDisplay {

  def build( gui:GUI, game:Game, full:Boolean ):List[Node]
}
