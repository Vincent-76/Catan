package de.htwg.se.catan.aview.gui

import de.htwg.se.catan.aview.gui.impl.game.ClassicGameStackPaneImpl
import de.htwg.se.catan.model._
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import scalafx.scene.layout._

/**
 * @author Vincent76;
 */
object GameStackPane:
  def get( game:Game ):GameStackPane[_] = game match
    case _:ClassicGameImpl => new ClassicGameStackPaneImpl()
    case c => throw new NotImplementedError( "GameStackPane[" + c.getClass.getName + "]" )

trait GameStackPane[T <: Game] extends VBox:

  def update( game:Game ):Unit = doUpdate( game.asInstanceOf[T] )

  protected def doUpdate( game:T ):Unit