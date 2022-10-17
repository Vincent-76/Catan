package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.{ Card, DevelopmentCard, InvalidDevCard }
import com.aimit.htwg.catan.view.tui.TUI.InvalidFormat
import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput, TUI }

/**
 * @author Vincent76;
 */
case object UseDevCommand
  extends CommandAction( "usedevcard", List( "devcard" ), "Use one of your development cards." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit = commandInput.args.headOption match {
    case Some( devCardString ) =>
      val devCard = DevelopmentCard.usableOf( devCardString )
      if ( devCard.isEmpty )
        controller.error( InvalidDevCard( devCardString ) )
      else
        controller.useDevCard( devCard.get )
    case None => controller.error( InvalidFormat( commandInput.input ) )
  }

  override protected def getInputPattern:String = TUI.regexIgnoreCase( command ) + " (" +
    DevelopmentCard.impls.filter( _.usable ).map( d => TUI.regexIgnoreCase( d.title ) ).mkString( "|" ) + ")"

}
