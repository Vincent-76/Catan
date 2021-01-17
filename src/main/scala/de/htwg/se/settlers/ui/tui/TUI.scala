package de.htwg.se.settlers.ui.tui

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.ui.tui.command.{ CommandAction, ExitCommand, HelpCommand }
import de.htwg.se.settlers.ui.tui.phaseaction._
import de.htwg.se.settlers.util._

import scala.util.Try

/**
 * @author Vincent76;
 */
object TUI {
  val reset:String = Console.RESET

  val commands = List(
    HelpCommand,
    ExitCommand
  )

  val textOnColor:String = Console.BLACK

  val text:String = Console.WHITE

  val background:String = Console.WHITE_B

  val errorColor:String = Console.RED


  val resourcePattern:String = "(\\s*(" + Resources.get.map( r => TUI.regexIgnoreCase( r.s ) ).mkString( "|" ) + ")\\s*[1-9][0-9]*\\s*)"

  val resourcePatternInfo:String = "<" + Resources.get.map( _.s ).mkString( "|" ) + "> + <amount>"

  def clear( ):Unit = {
    /*val os = System.getProperty( "os.name" )
    if ( os.contains( "Windows" ) ) new ProcessBuilder( "cmd", "/c", "cls" ).inheritIO.start.waitFor
    else {
      Runtime.getRuntime.exec( "clear" )
      Runtime.getRuntime.exec( "reset" )
    }*/
    ( 1 to 50 ).foreach( _ => println() )
  }

  def out( o:Any ):Unit = print( TUI.reset + o )

  def outln( o:Any ):Unit = println( TUI.reset + o )

  def error( o:Any ):Unit = {
    println( TUI.reset + errorColor + o )
  }

  def action( s:String ):Unit = outln( "> " + s + " <" )

  def awaitKey( s:String = "Press Enter to proceed" ):Unit = {
    action( s )
    scala.io.StdIn.readLine()
  }

  def confirmed( actionInfo:String = "Type [Y] for yes or [N] for no" ):Boolean = {
    while ( true ) {
      action( actionInfo )
      val input = scala.io.StdIn.readLine().toLowerCase
      input match {
        case "y" => return true
        case "n" => return false
        case _ =>
      }
    }
    false
  }

  def regexIgnoreCase( s:String ):String = {
    if ( s.nonEmpty )
      return ( Seq() ++ s.toCharArray ).red( "", ( s:String, c:Char ) => s + "[" + c.toUpper + c.toLower + "]" )
    s
  }

  def displayName( p:Player, toLength:Int = -1 ):String = {
    val length = if( toLength >= 0 ) toLength else p.idName.length
    p.color.c.t + p.idName.toLength( length ) + reset
  }

  def parseResources( resourcesString:String ):ResourceCards = {
    val parts = resourcesString.split( "\\s*,\\s*" ).toList
    parts.red( ResourceCards.empty, ( cards:ResourceCards, part:String ) => {
      val res = parseResource( part )
      if ( res.isDefined )
        cards.updated( res.get._1, res.get._2 )
      else cards
    } )
  }

  def parseResource( resourceString:String ):Option[(Resource, Int)] = {

    val data = resourceString.splitAt( "[0-9]".r.findFirstMatchIn( resourceString ).map( _.start ).getOrElse( 0 ) )
    val resource = Resources.of( data._1.removeSpaces() )
    if ( resource.isDefined && Try( data._2.toInt ).isSuccess )
      Some( resource.get, data._2.toInt )
    else Option.empty
  }
}

class TUI( val controller:Controller ) {

  TUI.clear()
  TUI.outln( "Loading ..." )

  def start( ):Unit = {
    while ( controller.running )
      listen()
    if ( controller.game.winner.isDefined ) {
      val winner = controller.player( controller.game.winner.get )
      TUI.out( TUI.displayName( winner ) + " won with " + winner.getVictoryPoints( controller.game ) + " victory points!" )
      TUI.awaitKey( "Press Enter to quit" )
    }
  }

  def listen( ):Boolean = {
    val phaseAction = controller.game.phase match {
      case InitPhase => InitAction( controller )
      case InitPlayerPhase => InitPlayerAction( controller )
      case InitBeginnerPhase => InitBeginnerAction( controller )
      case InitBuildSettlementPhase => InitBuildSettlementAction( controller )
      case p:InitBuildRoadPhase => InitBuildRoadAction( p, controller )
      case NextPlayerPhase => NextPlayerAction( controller )
      case TurnStartPhase => TurnStartAction( controller )
      case DicePhase => DiceAction( controller )
      case _:DropResourceCardPhase => DropResourceCardAction( controller )
      case g:GatherPhase => GatherAction( g, controller )
      case r:RobberPlacePhase => RobberPlaceAction( r, controller )
      case r:RobberStealPhase => RobberStealAction( r, controller )
      case ActionPhase => ActionAction( controller )
      case b:BuildPhase => BuildAction( b, controller )
      case t:PlayerTradePhase => PlayerTradeAction( t, controller )
      case _:DevYearOfPlentyPhase => DevYearOfPlentyAction( controller )
      case p:DevRoadBuildingPhase => DevRoadBuildingAction( p, controller )
      case _:DevMonopolyPhase => DevMonopolyAction( controller )
      case _ =>
        TUI.outln( "Error!" )
        TUI.outln( "Press Enter to exit game" )
        TUI.awaitKey()
        controller.exit()
        return false
      /*("Press Enter to undo", Option.empty, _ => {
        if ( !controller.undo() ) {
          outln( "Game Crash!" )
          Some( "Type [help] for possible actions" )
        } else
          Option.empty
      })*/
    }
    handlePhaseAction( phaseAction )
    controller.running
  }

  private def handlePhaseAction( phaseAction:PhaseAction, error:Option[(String, String)] = Option.empty ):Unit = {
    TUI.clear()
    if ( phaseAction.gameDisplay.isDefined )
      TUI.out( phaseAction.gameDisplay.get )
    val actionInfo = phaseAction.actionInfo
    if ( actionInfo.isEmpty )
      return
    println()
    if ( error.isDefined ) {
      TUI.error( error.get._2 + " [" + error.get._1 + "]" )
    }
    TUI.action( actionInfo.get )
    val commandInput = CommandInput( scala.io.StdIn.readLine )
    val globalCommand = findGlobalCommand( commandInput )
    if ( globalCommand.isDefined ) {
      if ( commandInput.input.matches( globalCommand.get.inputPattern ) )
        globalCommand.get.action( commandInput, controller ) match {
          case Some( e:ControllerError ) => handlePhaseAction( phaseAction, Some( commandInput.input, getErrorMessage( e ) ) )
          case Some( t ) => handlePhaseAction( phaseAction, Some( commandInput.input, "Error! " + t ) )
          case _ =>
        }
      else
        handlePhaseAction( phaseAction, Some( commandInput.input, "Invalid format!" ) )
      return
    }
    val inputPattern = phaseAction.inputPattern
    if ( inputPattern.isEmpty || commandInput.input.matches( inputPattern.get ) ) {
      phaseAction.action( commandInput ) match {
        case Some( e:ControllerError ) => handlePhaseAction( phaseAction, Some( commandInput.input, getErrorMessage( e ) ) )
        case Some( t ) => handlePhaseAction( phaseAction, Some( commandInput.input, "Error! " + t ) )
        case _ =>
      }
    } else
      handlePhaseAction( phaseAction, Some( commandInput.input, "Invalid format!" ) )
  }

  def getErrorMessage( controllerError:ControllerError ):String = controllerError match {
    case NotEnoughPlayers => "Minimum 3 Players required!"
    case InvalidPlacementPoint => "Invalid place!"
    case _ => controllerError.getClass.getSimpleName
  }

  def findGlobalCommand( commandInput:CommandInput ):Option[CommandAction] = {
    if ( commandInput.command.isDefined )
      return TUI.commands.find( _.command == commandInput.command.get )
    Option.empty
  }
}