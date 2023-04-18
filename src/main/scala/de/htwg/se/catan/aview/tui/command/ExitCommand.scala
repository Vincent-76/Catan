package de.htwg.se.catan.aview.tui.command

import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.aview.tui.{ CommandAction, CommandInput, TUI }

/**
 * @author Vincent76;
 */
case object ExitCommand
  extends CommandAction( "exit", List.empty, "Exit the game." ):

  override def action( commandInput:CommandInput, controller:Controller ):Unit =
    /*TUI.clear()
    TUI.outln( Console.YELLOW + "Do you want to exit?" )
    if ( TUI.confirmed() ) {
      TUI.outln( "Bye..." )
      state.exit()
    } else state.show()*/
    controller.exit()
  