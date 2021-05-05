package de.htwg.se.settlers.aview.tui.command

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.aview.tui.{ CommandAction, CommandInput, TUI }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */

case object HelpCommand
  extends CommandAction( "help", List.empty, "Lists all available commands." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit = {
    TUI.clear()
    val commands = TUI.commands
    TUI.outln( "Commands:" )
    val cLength = commands.maxBy( _.command.length ).command.length
    val pLength = commands.maxBy( _.parameter.size ).use( c => ( c.parameter.size * 4 - 2 ).validate( _ >= 0, 0 ) + c.parameter.sumLength )
    commands.foreach( c => TUI.outln( c.command.toLength( cLength ) + "\t" + c.parameter.map( p => "<" + p + ">" ).mkString( " " ).toLength( pLength ) + "\t->\t" + c.desc ) )
    //TUI.awaitKey()
    //state.show()
  }
}
