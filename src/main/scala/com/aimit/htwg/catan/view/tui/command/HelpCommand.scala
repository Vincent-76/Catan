package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info
import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput, TUI }
import com.aimit.htwg.catan.util._

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */

case object HelpCommand
  extends CommandAction( "help", List.empty, "Lists all available commands." ) {

  override def action( commandInput:CommandInput, controller:Controller ):(Try[Option[Info]], List[String]) = {
    val commands = TUI.commands
    List( "Commands:" )
    val cLength = commands.maxBy( _.command.length ).command.length
    val pLength = commands.maxBy( _.parameter.size ).use( c => ( c.parameter.size * 4 - 2 ).validate( _ >= 0, 0 ) + c.parameter.sumLength )
    (Success( None ), List( "Commands: " ) ++ commands.map( c => c.command.toLength( cLength ) + "\t" + c.parameter.map( p => "<" + p + ">" ).mkString( " " ).toLength( pLength ) + "\t->\t" + c.desc ))

  }
}
