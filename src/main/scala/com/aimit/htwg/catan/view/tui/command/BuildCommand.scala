package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput, TUI }
import com.aimit.htwg.catan.model
import com.aimit.htwg.catan.model.{ Info, StructurePlacement }
import com.aimit.htwg.catan.util.RichIterable

import scala.util.Try

/**
 * @author Vincent76;
 */
case object BuildCommand
  extends CommandAction( "build", List( StructurePlacement.impls.map( _.title ).mkString( "|" ) ), "Build a new structure." ) {

  override def action( commandInput:CommandInput, controller:Controller ):(Try[Option[Info]], List[String]) = {
    val placement = controller.game.availablePlacements.withType[model.StructurePlacement].find( _.title.toLowerCase == commandInput.args( 0 ).toLowerCase )
    (controller.action( _.setBuildState( placement.get ) ), Nil)
  }

  override protected def getInputPattern:String = TUI.regexIgnoreCase( command ) +
    "\\s+(" + StructurePlacement.impls.map( p => TUI.regexIgnoreCase( p.title ) ).mkString( "|" ) + ")"
}
