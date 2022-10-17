package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput }

/**
 * @author Vincent76;
 */
case object BuyDevCommand extends
  CommandAction( "buydevcard", List.empty, "Buy a development card." ) {

  override def action( commandInput:CommandInput, controller:Controller ):Unit =
    controller.buyDevCard()

}
