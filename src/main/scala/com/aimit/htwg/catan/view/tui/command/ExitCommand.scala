package com.aimit.htwg.catan.view.tui.command

import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.Info
import com.aimit.htwg.catan.view.tui.{ CommandAction, CommandInput, TUI }

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */
case object ExitCommand
  extends CommandAction( "exit", List.empty, "Exit the game." ) {

  override def action( commandInput:CommandInput, controller:Controller ):(Try[Option[Info]], List[String]) = {
    /*TUI.clear()
    TUI.outln( Console.YELLOW + "Do you want to exit?" )
    if ( TUI.confirmed() ) {
      TUI.outln( "Bye..." )
      state.exit()
    } else state.show()*/
    (controller.exit(), Nil)
  }
}
