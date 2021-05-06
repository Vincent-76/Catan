package de.htwg.se.settlers.aview.tui.command

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.aview.tui.{ CommandAction, CommandInput }

/**
 * @author Vincent76;
 */
case object BuyDevCommand extends
  CommandAction( "buydevcard", List.empty, "Buy a development card." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit =
    controller.buyDevCard()

}
