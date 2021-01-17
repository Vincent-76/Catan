package de.htwg.se.settlers.model

/**
 * @author Vincent76;
 */
case class Color( t:String, b:String )


object ColorBlack extends Color( Console.BLACK, Console.BLACK_B )

object ColorRed extends Color( Console.RED, Console.RED_B )

object ColorGreen extends Color( Console.GREEN, Console.GREEN_B )

object ColorYellow extends Color( Console.YELLOW, Console.YELLOW_B )

object ColorBlue extends Color( Console.BLUE, Console.BLUE_B )

object ColorMagenta extends Color( Console.MAGENTA, Console.MAGENTA_B )

object ColorCyan extends Color( Console.CYAN, Console.CYAN_B )

object ColorWhite extends Color( Console.WHITE, Console.WHITE_B )


sealed abstract class PlayerColor( val name:String, val c:Color )


case object Green extends PlayerColor( "Green", ColorGreen )

case object Red extends PlayerColor( "Red", ColorRed )

case object Magenta extends PlayerColor( "Magenta", ColorMagenta )

case object Cyan extends PlayerColor( "Cyan", ColorCyan )
