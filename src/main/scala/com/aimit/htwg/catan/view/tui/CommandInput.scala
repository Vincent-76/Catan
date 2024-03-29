package com.aimit.htwg.catan.view.tui

/**
 * @author Vincent76;
 */

case class CommandInput( input:String ) {
  val split:Vector[String] = input.split( "\\s" ).toVector
  val command:Option[String] = if ( split.nonEmpty ) Some( split.head ) else Option.empty
  val args:Vector[String] = if ( split.length > 1 ) split.tail else Vector.empty
}
