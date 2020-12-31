package de.htwg.se.munchkin.ui

import de.htwg.se.munchkin.controller.Controller
import de.htwg.se.munchkin.model._
import de.htwg.se.munchkin.util._

/**
 * @author Vincent76;
 */
class TUI( val controller:Controller ) {

  class CommandInput( val input:String ) {
    val split:Vector[String] = input.split( "\\s" ).toVector
    val command:Option[String] = if( split.nonEmpty ) Some( split.head ) else Option.empty
    val args:Vector[String] = if( split.length > 1 ) split.tail else Vector()
  }

  type CommandWaiter = (String, Option[String], CommandInput => Option[String])

  val commands = List(
    HelpCommand,
    ViewCommand,
    UndoCommand,
    RedoCommand,
    StopCommand,
    SaveCommand,
    LoadCommand,
    ExitCommand
  )

  while ( controller.running ) {
    clear()
    val commandWaiter:CommandWaiter = controller.game.phase match {
      case InitPhase => initPhase
      case PlayerPhase => playerPhase
      case TurnPhase => turnPhase
      case BeginPhase => beginPhase
      case DoorPhase => doorPhase
      case FightPhase => ???
      case CursePhase => ???
      case LootPhase => ???
      case ChallengePhase => ???
      case EndPhase => ???
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
    if( commandInput.command.isDefined ) {
      val commandOption = commands.find( _.command == commandInput.command.get )
      if ( commandOption.isDefined ) {
        commandOption.get.action( commandInput.args )
        return true
      }
    }
    false
  }

  private def initPhase:CommandWaiter = {
    println( "Welcome to Munchkin!" )
    println( "Type [help] for a list of all available commands." )
    ("Press Enter to add players", Option.empty, _ => {
      controller.setPlayerPhase()
      Option.empty
    })
  }

  private def playerPhase:CommandWaiter = {
    println( "Players:" )
    for ( player <- controller.game.players ) {
      println( "\n" + player.display )
    }
    ("Type [<name> <" + Gender.stringList + ">] to add a player, or [next] to continue", Some( "(" + regexIgnoreCase( "next" ) + "|[a-zA-Z0-9]+\\s[" + Gender.regexToken + "])" ), ( command:CommandInput ) => {
      if ( !command.input.matches( regexIgnoreCase( "next" ) ) ) {
        controller.addPlayer( command.split( 0 ), Gender.fromShort( command.split( 1 ) ) )
        Option.empty
      } else if ( !controller.setTurnPhase() ) {
        Some( "Minimum 2 Players required!" )
      } else
        Option.empty
    })
  }

  private def turnPhase:CommandWaiter = {
    println( "Time to dice who begins!" )
    ("Press Enter to roll the dices", Option.empty, _ => {
      val id = diceOutBeginner( controller.game.players.indices.map( _ => controller.rollDice ) )
      controller.startGame( id )
      println( "\n->\t<" + id + ">" + controller.player( id ).name + " begins.\n" )
      awaitKey()
      Option.empty
    })
  }

  private def diceOutBeginner( values:Seq[Int] ):Int = {
    println( "Values: " )
    val nameLength = controller.game.players.maxBy( _.name.length ).name.length + 5
    for ( (x, i) <- values.view.zipWithIndex )
      if ( x > 0 ) println( "<" + i + ">" + ( controller.game.players( i ).name + ":" ).toLength( nameLength ) + x )
    val maxValue = values.max
    val beginners = values.count( _ >= maxValue )
    if ( beginners > 1 ) {
      println( "Tie!" )
      awaitKey( "Press enter to roll again" )
      return diceOutBeginner( values.map( value => if ( value < maxValue ) 0 else controller.rollDice ) )
    }
    values.indexOf( maxValue )
  }

  private def beginPhase:CommandWaiter = {
    print( GameDisplay( controller.game ).buildBeginPhase )
    ("Hand the game to " + controller.player.name + " and press Enter to continue", Option.empty, _ => {
      controller.setDoorPhase()
      Option.empty
    })
  }

  private def doorPhase:CommandWaiter = {
    print( GameDisplay( controller.game ).buildDoorPhase )
    ("Press Enter to draw a door card", Option.empty, _ => {
      val cardOption = controller.drawDoorCard()
      if ( cardOption.isDefined ) {
        ???
      } else
        awaitKey( "No more DoorCards, press Enter to exit!" )
      Option.empty
    })
  }

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

  private def regexIgnoreCase( s:String ):String = {
    if ( s.nonEmpty )
      return ( Seq() ++ s.toCharArray ).red( "", ( s:String, c:Char ) => s + "[" + c.toUpper + c.toLower + "]" )
    s
  }

  private def action( s:String ):Unit = println( "> " + s + " <" )


  sealed abstract case class Command( command:String, parameter:List[String] = List(), desc:String ) {
    def action( args:Vector[String] ):Unit

    def getSyntax:String = "[<" + command + "> " + parameter.map( p => "<" + p + ">" ).mkString( " " )
  }


  object HelpCommand extends Command( "help", List(), "Lists all available commands" ) {
    override def action( args:Vector[String] ):Unit = {
      clear()
      println( "Commands:" )
      val cLength = commands.maxBy( _.command.length ).command.length
      val pLength = commands.maxBy( _.parameter.size ).use( c => ( c.parameter.size * 4 - 2 ).check( _ >= 0, 0 ) + c.parameter.sumLength )
      commands.foreach( c => println( c.command.toLength( cLength ) + "\t" + c.parameter.map( p => "<" + p + ">" ).mkString( " " ).toLength( pLength ) + "\t->\t" + c.desc ) )
      awaitKey()
    }
  }

  object ViewCommand extends Command( "view", List( "id" ), "Details view on the card with the given <id>" ) {
    override def action( args:Vector[String] ):Unit = {
      if ( args.isEmpty || args.head.toIntOption.isEmpty ) {
        println( "Invalid format, type " + getSyntax )
        return
      }
      val card = controller.viewCard( controller.game.turn, args.head.toInt )
      if( card.isEmpty ) {
        println( "You can't see this card!" )
        return
      }
      clear()
      println( card.get.display( true ) + "\n" )
      awaitKey()
    }
  }

  object UndoCommand extends Command( "undo", List(), "Undo your last action" ) {
    override def action( args:Vector[String] ):Unit = {
      println( "Do you want to undo your last action?" )
      if ( confirmed ) {
        if ( controller.undo() )
          println( "Undo successful." )
        else
          println( "Nothing to undo!" )
        awaitKey()
      }
    }
  }

  object RedoCommand extends Command( "redo", List(), "Redo your last undone action" ) {
    override def action( args:Vector[String] ):Unit = {
      println( "Do you want to redo your last undone action?" )
      if ( confirmed ) {
        if ( controller.redo() )
          println( "Redo successful." )
        else
          println( "Nothing to redo!" )
        awaitKey()
      }
    }
  }

  object StopCommand extends Command( "stop", List(), "Stops the game and shows the winner" ) {
    override def action( args:Vector[String] ):Unit = {
      ???
    }
  }

  object SaveCommand extends Command( "save", List(), "Save your actual game" ) {
    override def action( args:Vector[String] ):Unit = {
      ???
    }
  }

  object LoadCommand extends Command( "load", List(), "Load your last game" ) {
    override def action( args:Vector[String] ):Unit = {
      ???
    }
  }

  object ExitCommand extends Command( "exit", List(), "Exit the game" ) {
    override def action( args:Vector[String] ):Unit = {
      println( "Do you want to exit?" )
      if ( confirmed ) {
        println( "Bye..." )
        controller.exit()
      }
    }
  }

}
