package de.htwg.se.settlers.ui.tui.phaseaction

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Resources
import de.htwg.se.settlers.ui.tui.{ CommandInput, GameDisplay, TUI }

import scala.util.{ Failure, Success }

/**
 * @author Vincent76;
 */
case class DevMonopolyAction( controller:Controller ) extends PhaseAction( controller ) {

  override def getGameDisplay:Option[String] = Some( GameDisplay( controller ).buildGameField )

  override def actionInfo:Option[String] = {
    TUI.outln( "You can specify a resource to get all the corresponding cards from the other players" )
    Some( "Type [<" + Resources.get.map( _.s ).mkString( "|" ) + ">] to specify a resource" )
  }

  override def inputPattern:Option[String] =
    Some( "(" + Resources.get.map( r => TUI.regexIgnoreCase( r.s ) ).mkString( "|" ) + ")" )

  override def action( commandInput:CommandInput ):Option[Throwable] = {
    val resource = Resources.of( commandInput.input ).get
    controller.monopolyAction( resource ) match {
      case Success( amount ) =>
        println()
        TUI.outln( "You got " + amount + " " + resource.s + "." )
        Option.empty
      case Failure( e ) => Some( e )
    }
  }
}
