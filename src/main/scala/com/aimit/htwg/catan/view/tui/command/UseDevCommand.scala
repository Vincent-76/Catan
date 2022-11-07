package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.{ Card, DevelopmentCard, Info, InvalidDevCard }
import com.aimit.htwg.catan.view.tui.TUI.InvalidFormat
import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput, TUI }

import scala.util.{ Failure, Try }

/**
 * @author Vincent76;
 */
case object UseDevCommand
  extends CommandAction( "usedevcard", List( "devcard" ), "Use one of your development cards." ) {

  override def action( commandInput:CommandInput, controller:Controller ):(Try[Option[Info]], List[String]) = commandInput.args.headOption match {
    case Some( devCardString ) =>
      val devCard = DevelopmentCard.usableOf( devCardString )
      if ( devCard.isEmpty )
        (Failure( controller.error( InvalidDevCard( devCardString ) ) ), Nil)
      else
        (controller.action( _.useDevCard( devCard.get ) ), Nil)
    case None => (Failure( controller.error( InvalidFormat( commandInput.input ) ) ), Nil)
  }

  override protected def getInputPattern:String = TUI.regexIgnoreCase( command ) + " (" +
    DevelopmentCard.impls.filter( _.usable ).map( d => TUI.regexIgnoreCase( d.title ) ).mkString( "|" ) + ")"

}
