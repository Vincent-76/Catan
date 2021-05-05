package de.htwg.se.settlers.aview.tui.command

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Cards, InvalidDevCard }
import de.htwg.se.settlers.aview.tui.TUI.InvalidFormat
import de.htwg.se.settlers.aview.tui.{ CommandAction, CommandInput, TUI }

/**
 * @author Vincent76;
 */
case object UseDevCommand
  extends CommandAction( "usedevcard", List( "devcard" ), "Use one of your development cards." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit = commandInput.args.headOption match {
    case Some( devCardString ) =>
      val devCard = Cards.usableDevCardOf( devCardString )
      if ( devCard.isEmpty )
        controller.error( InvalidDevCard( devCardString ) )
      else
        controller.game.state.useDevCard( devCard.get )
    case None => controller.error( InvalidFormat( commandInput.input ) )
  }

  override protected def getInputPattern:String = TUI.regexIgnoreCase( command ) + " (" +
    Cards.devCards.filter( _.usable ).map( d => TUI.regexIgnoreCase( d.title ) ).mkString( "|" ) + ")"

}