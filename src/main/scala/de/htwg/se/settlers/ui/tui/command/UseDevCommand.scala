package de.htwg.se.settlers.ui.tui.command

import de.htwg.se.settlers.model.{ Cards, InvalidDevCard, State }
import de.htwg.se.settlers.ui.tui.TUI.InvalidFormat
import de.htwg.se.settlers.ui.tui.{ CommandAction, CommandInput, TUI }

/**
 * @author Vincent76;
 */
case object UseDevCommand
  extends CommandAction( "usedevcard", List( "devcard" ), "Use one of your development cards." ) {

  override def action( commandInput:CommandInput, state:State ):Unit = commandInput.args.headOption match {
    case Some( devCardString ) =>
      val devCard = Cards.usableDevCardOf( devCardString )
      if ( devCard.isEmpty )
        state.onError( InvalidDevCard( devCardString ) )
      else
        state.useDevCard( devCard.get )
    case None => state.onError( InvalidFormat( commandInput.input ) )
  }

  override protected def getInputPattern:String = TUI.regexIgnoreCase( command ) + " (" +
    Cards.devCards.filter( _.usable ).map( d => TUI.regexIgnoreCase( d.t ) ).mkString( "|" ) + ")"

}
