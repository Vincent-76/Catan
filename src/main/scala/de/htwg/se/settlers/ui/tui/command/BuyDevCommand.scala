package de.htwg.se.settlers.ui.tui.command

import de.htwg.se.settlers.model.State
import de.htwg.se.settlers.ui.tui.{ CommandAction, CommandInput }

/**
 * @author Vincent76;
 */
case object BuyDevCommand extends
  CommandAction( "buydevcard", List.empty, "Buy a development card." ){

  override def action( commandInput:CommandInput, state:State ):Unit = state.buyDevCard()

}
