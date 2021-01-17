package de.htwg.se.settlers.ui.tui.command
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ Cards, InvalidDevCard }
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI }

/**
 * @author Vincent76;
 */
case object UseDevCommand extends CommandAction( "usedevcard", List( "devcard" ), "Use one of your development cards." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Option[Throwable] = {
    val devCard = Cards.usableDevCardOf( commandInput.args.head )
    if( devCard.isEmpty )
      Some( InvalidDevCard )
    else
      controller.useDevCard( devCard.get )
  }

  override protected def getInputPattern:String = TUI.regexIgnoreCase( command ) + " (" +
    Cards.devCards.filter( _.usable ).map( d => TUI.regexIgnoreCase( d.t ) ).mkString( "|" ) + ")"

}
