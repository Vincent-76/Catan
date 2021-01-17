package de.htwg.se.settlers.ui.tui.command
import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.ui.tui.{ CommandInput, TUI }

/**
 * @author Vincent76;
 */
case object BuyDevCommand extends CommandAction( "buydevcard", List.empty, "Buy a development card." ){

  override def action( commandInput:CommandInput, controller:Controller ):Option[Throwable] = controller.buyDevCard() match {
    case Some( t ) => Some( t )
    case None =>
      val devCard = controller.turn.getLastDrawnDevCard
      println()
      TUI.outln( "Drawn: " + devCard.get.t + "\n" + devCard.get.desc )
      println()
      TUI.awaitKey()
      Option.empty
  }

}
