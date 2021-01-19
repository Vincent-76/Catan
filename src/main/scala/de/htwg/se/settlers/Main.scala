package de.htwg.se.settlers

import de.htwg.se.settlers.controller.Controller

/**
 * @author Vincent76;
 */
object Main {
  def main( args:Array[String] ):Unit = {
    new Controller( "TUI" )
  }
}
