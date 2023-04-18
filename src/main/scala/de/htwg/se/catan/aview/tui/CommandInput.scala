package de.htwg.se.catan.aview.tui

/**
 * @author Vincent76;
 */

case class CommandInput( input:String ):
  val split:Vector[String] = input.split( "\\s" ).toVector
  val command:Option[String] = if split.nonEmpty then Some( split.head ) else Option.empty
  val args:Vector[String] = if split.length > 1 then split.tail else Vector.empty
