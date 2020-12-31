package de.htwg.se.settlers.ui

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.{ InitPhase, PlayerPhase }
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
class TUI( val controller:Controller ) {

  class CommandInput( val input:String ) {
    val split:Vector[String] = input.split( "\\s" ).toVector
    val command:Option[String] = if ( split.nonEmpty ) Some( split.head ) else Option.empty
    val args:Vector[String] = if ( split.length > 1 ) split.tail else Vector()
  }

  type CommandWaiter = (String, Option[String], CommandInput => Option[String])

  val commands = List(
    HelpCommand
  )

  while ( controller.running ) {
    clear()
    val commandWaiter:CommandWaiter = controller.game.phase match {
      case InitPhase => ???
      case PlayerPhase => ???
      case _ => error
    }
    handleCommandWaiter( commandWaiter )
  }

  private def handleCommandWaiter( commandWaiter:CommandWaiter ):Unit = {
    while ( true ) {
      println()
      action( commandWaiter._1 )
      val commandInput = new CommandInput( scala.io.StdIn.readLine )
      if ( checkGlobalCommand( commandInput ) )
        return
      if ( commandWaiter._2.isEmpty || commandInput.input.matches( commandWaiter._2.get ) ) {
        val result = commandWaiter._3( commandInput )
        if ( result.isDefined )
          println( result.get )
        else
          return
      } else
        println( "Invalid format!" )
    }
  }

  private def clear( ):Unit = {
    val os = System.getProperty( "os.name" )
    if ( os.contains( "Windows" ) ) new ProcessBuilder( "cmd", "/c", "cls" ).inheritIO.start.waitFor
    else Runtime.getRuntime.exec( "clear" )
  }

  private def getParameter( command:String ):Vector[String] = command.split( "\\s" ).toVector.tail

  private def awaitKey( s:String = "Press Enter to proceed" ):Unit = {
    action( s )
    scala.io.StdIn.readLine()
  }

  private def confirmed:Boolean = {
    while ( true ) {
      action( "Type [Y] to confirm or [N] to abort" )
      val input = scala.io.StdIn.readChar().toLower
      input match {
        case 'y' => return true
        case 'n' => return false
        case _ =>
      }
    }
    false
  }

  private def checkGlobalCommand( commandInput:CommandInput ):Boolean = {
    if ( commandInput.command.isDefined ) {
      val commandOption = commands.find( _.command == commandInput.command.get )
      if ( commandOption.isDefined ) {
        commandOption.get.action( commandInput.args )
        return true
      }
    }
    false
  }

  private def regexIgnoreCase( s:String ):String = {
    if ( s.nonEmpty )
      return ( Seq() ++ s.toCharArray ).red( "", ( s:String, c:Char ) => s + "[" + c.toUpper + c.toLower + "]" )
    s
  }

  private def action( s:String ):Unit = println( "> " + s + " <" )

  private def error:CommandWaiter = {
    println( "Error!" )
    ("Press Enter to undo", Option.empty, _ => {
      if ( !controller.undo() ) {
        println( "Game Crash!" )
        Some( "Type [help] for possible actions" )
      } else
        Option.empty
    })
  }


  sealed abstract case class Command( command:String, parameter:List[String] = List(), desc:String ) {
    def action( args:Vector[String] ):Unit

    def getSyntax:String = "[<" + command + "> " + parameter.map( p => "<" + p + ">" ).mkString( " " )
  }


  object HelpCommand extends Command( "help", List(), "Lists all available commands" ) {
    override def action( args:Vector[String] ):Unit = {
      clear()
      println( "Commands:" )
      val cLength = commands.maxBy( _.command.length ).command.length
      val pLength = commands.maxBy( _.parameter.size ).use( c => ( c.parameter.size * 4 - 2 ).validate( _ >= 0, 0 ) + c.parameter.sumLength )
      commands.foreach( c => println( c.command.toLength( cLength ) + "\t" + c.parameter.map( p => "<" + p + ">" ).mkString( " " ).toLength( pLength ) + "\t->\t" + c.desc ) )
      awaitKey()
    }
  }

}
