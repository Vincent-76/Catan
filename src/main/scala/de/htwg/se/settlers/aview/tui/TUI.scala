package de.htwg.se.settlers.aview.tui

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Player.{ Blue, Green, PlayerColor, Red, Yellow }
import de.htwg.se.settlers.model.state._
import de.htwg.se.settlers.model.{ PlacementPointNotEmpty, _ }
import de.htwg.se.settlers.aview.tui.TUI.InvalidFormat
import de.htwg.se.settlers.aview.tui.command.{ ExitCommand, HelpCommand, RedoCommand, UndoCommand }
import de.htwg.se.settlers.aview.tui.tuistate._
import de.htwg.se.settlers.util._

import scala.util.Try

/**
 * @author Vincent76;
 */
object TUI {
  val reset:String = Console.RESET

  val textOnColor:String = Console.BLACK

  val text:String = Console.WHITE

  val background:String = Console.WHITE_B

  val errorColor:String = Console.RED

  val resourcePattern:String = "(\\s*(" + Resources.get.map( r => regexIgnoreCase( r.title ) ).mkString( "|" ) + ")\\s*[1-9][0-9]*\\s*)"

  val resourcePatternInfo:String = "<" + Resources.get.map( _.title ).mkString( "|" ) + "> <amount>"

  val commands = List(
    HelpCommand,
    ExitCommand,
    UndoCommand,
    RedoCommand
  )

  case class InvalidFormat( input:String ) extends ControllerError


  def clear( ):Unit = {
    /*val os = System.getProperty( "os.name" )
    if ( os.contains( "Windows" ) ) new ProcessBuilder( "cmd", "/c", "cls" ).inheritIO.start.waitFor
    else {
      Runtime.getRuntime.exec( "clear" )
      Runtime.getRuntime.exec( "reset" )
    }*/
    ( 1 to 50 ).foreach( _ => println() )
  }

  def out( o:Any = "" ):Unit = print( reset + o )

  def outln( o:Any = "" ):Unit = println( reset + o )

  def error( o:Any = "" ):Unit = {
    println( reset + errorColor + o )
  }

  def action( s:String ):Unit = outln( "> " + s + " <" )

  /*def awaitKey( s:String = "Press Enter to proceed" ):Unit = {
    action( s )
    scala.io.StdIn.readLine()
  }*/

  /*def confirmed( actionInfo:String = "Type [Y] for yes or [N] for no" ):Boolean = {
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
  }*/

  def colorOf( playerColor:PlayerColor ):String = playerColor match {
    case Green => Console.GREEN
    case Red => Console.MAGENTA
    case Yellow => Console.YELLOW
    case Blue => Console.CYAN
  }

  def regexIgnoreCase( s:String ):String = {
    if ( s.nonEmpty )
      return ( Seq() ++ s.toCharArray ).red( "", ( s:String, c:Char ) => s + "[" + c.toUpper + c.toLower + "]" )
    s
  }

  def errorHighlight( o:Any = "" ):String = reset + o + Console.RED

  def displayName( p:Player, toLength:Int = -1 ):String = {
    val length = if ( toLength >= 0 ) toLength else p.idName.length
    colorOf( p.color ) + p.idName.toLength( length ) + reset
  }

  def parseResources( resourcesString:String ):ResourceCards = {
    val parts = resourcesString.split( "\\s*,\\s*" ).toList
    parts.red( ResourceCards.of(), ( cards:ResourceCards, part:String ) => {
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

class TUI( val controller:Controller ) extends Observer {

  var actionInfo:Option[String] = Option.empty

  controller.add( this )
  onUpdate( None )

  def onInput( input:String ):Unit = {
    val commandInput = CommandInput( input )
    val globalCommand = findGlobalCommand( commandInput )
    if ( globalCommand.isDefined ) {
      if ( commandInput.input.matches( globalCommand.get.inputPattern ) )
        globalCommand.get.action( commandInput, controller )
      else
        controller.error( InvalidFormat( commandInput.input ) )
    } else {
      val tuiState = findTUIState( controller.game.state )
      val inputPattern = tuiState.inputPattern
      if ( inputPattern.isEmpty || commandInput.input.matches( inputPattern.get ) )
        tuiState.action( commandInput )
      else
        controller.error( InvalidFormat( commandInput.input ) )
    }
  }

  override def onUpdate( info:Option[Info] ):Unit = {
    val tuiState = findTUIState( controller.game.state )
    TUI.clear()
    val gameDisplay = tuiState.getGameDisplay
    if ( gameDisplay.isDefined )
      TUI.out( gameDisplay.get )
    //TUI.outln()
    actionInfo = Some( tuiState.getActionInfo )
    if( info.isDefined ) {
      TUI.outln()
      onInfo( info.get )
    }
    TUI.outln()
    TUI.action( actionInfo.get )
  }

  def findGlobalCommand( commandInput:CommandInput ):Option[CommandAction] = {
    if ( commandInput.command.isDefined )
      return TUI.commands.find( _.command == commandInput.command.get )
    Option.empty
  }

  def findTUIState( state:State ):TUIState = state match {
    case s:InitState => InitTUIState( controller )
    case s:InitPlayerState => InitPlayerTUIState( controller )
    case s:InitBeginnerState => InitBeginnerTUIState( s.beginner, s.diceValues, controller )
    case s:BuildInitSettlementState => BuildInitSettlementTUIState( controller )
    case s:BuildInitRoadState => BuildInitRoadTUIState( s.settlementVID, controller )
    case s:NextPlayerState => NextPlayerTUIState( controller )
    case s:DiceState => DiceTUIState( controller )
    case s:DropHandCardsState => DropHandCardsTUIState( s.pID, controller )
    case s:RobberPlaceState => RobberPlaceTUIState( controller )
    case s:RobberStealState => RobberStealTUIState( s, controller )
    case s:ActionState => ActionTUIState( controller )
    case s:BuildState => BuildTUIState( s.structure, controller )
    case s:PlayerTradeState => PlayerTradeTUIState( s.pID, s.give, s.get, controller )
    case s:PlayerTradeEndState => PlayerTradeEndTUIState( s.give, s.get, s.decisions, controller )
    case s:YearOfPlentyState => YearOfPlentyTUIState( controller )
    case s:DevRoadBuildingState => DevRoadBuildingTUIState( controller )
    case s:MonopolyState => MonopolyTUIState( controller )
    case _ => ErrorTUIState( controller )
  }

  override def onInfo( info:Info ):Unit = {
    info match {
      case info:DiceInfo => TUI.outln( info.dices._1 + " + " + info.dices._2 + " = " + ( info.dices._1 + info.dices._2 ) )
      case GatherInfo( dices, playerResources ) =>
        TUI.outln( dices._1 + " + " + dices._2 + " = " + ( dices._1 + dices._2 ) )
        playerResources.foreach( d => {
          TUI.outln( TUI.displayName( controller.player( d._1 ) ) + " " + d._2.toString( "+" ) )
        } )
      case GotResourcesInfo( pID, cards ) =>
        TUI.outln( TUI.displayName( controller.player( pID ) ) + "  " + cards.toString( "+" ) )
      case LostResourcesInfo( pID, cards ) =>
        TUI.outln( TUI.displayName( controller.player( pID ) ) + "  " + cards.toString( "-" ) )
      case ResourceChangeInfo( playerAdd, playerSub ) =>
        val nameLength = ( playerAdd.keys ++ playerSub.keys ).map( controller.player( _ ).idName.length ).max
        playerSub.foreach( d => TUI.outln( TUI.displayName( controller.player( d._1 ), nameLength ) + "  " + d._2.toString( "-" ) ) )
        playerAdd.foreach( d => TUI.outln( TUI.displayName( controller.player( d._1 ), nameLength ) + "  " + d._2.toString( "+" ) ) )
      case BankTradedInfo( _, give, get ) =>
        TUI.outln( "You traded " + give.toString( "" ) + " for " + get.toString( "" ) + "." )
      case DrawnDevCardInfo( _, devCard ) =>
        TUI.outln( "Drawn: " + devCard.title + "\n" + devCard.desc )
      case InsufficientStructuresInfo( _, structure ) =>
        TUI.outln( "You don't have enough structures of " + structure.title + " to build more." )
      case NoPlacementPointsInfo( _, structure ) =>
        TUI.outln( "There aren't any more possible placement points for structure " + structure.title + " to build more." )
      case GameEndInfo( winner ) =>
        val p = controller.player( winner )
        TUI.out( TUI.displayName( p ) + " won with " + controller.game.getPlayerVictoryPoints( p.id ) + " victory points!" )
      case _ => return
    }
  }

  override def onError( t:Throwable ):Unit = {
    TUI.error( t match {
      case e:InvalidFormat => "Invalid format: [" + TUI.errorHighlight( e.input ) + "]!"
      case WrongState => "Unable in this state!"
      case InsufficientResources => "Insufficient resources for this action!"
      case TradePlayerInsufficientResources => "Trade player has insufficient resources for this action!"
      case InsufficientStructures( structure ) =>
        "Insufficient structures of " + TUI.errorHighlight( structure.title ) + " for this action!"
      case NonExistentPlacementPoint( id ) => "Placement point " + TUI.errorHighlight( id ) + " does not exists!"
      case PlacementPointNotEmpty( id ) => "Placement point " + TUI.errorHighlight( id ) + " is not empty!"
      case NoAdjacentStructure => "Player has no adjacent structure!"
      case TooCloseToBuilding( id ) => "Placement point " + TUI.errorHighlight( id ) + " is too close to another building!"
      case NoConnectedStructures( id ) => "No connected structures on placement point " + TUI.errorHighlight( id ) + "!"
      case SettlementRequired( id ) =>
        "You need a settlement on placement point " + TUI.errorHighlight( id ) + " to build a city!"
      case InvalidPlacementPoint( id ) => "Invalid placement point " + TUI.errorHighlight( id ) + "!"
      case NotEnoughPlayers => "Minimum " + TUI.errorHighlight( Game.minPlayers ) + " players required!"
      case InvalidPlayerColor( color ) => "Invalid player color: [" + TUI.errorHighlight( color ) + "]!"
      case RobberOnlyOnLand => "Robber can only be placed on land!"
      case NoPlacementPoints( structure ) =>
        "No available placement points for structure " + TUI.errorHighlight( structure.title ) + "!"
      case InvalidResourceAmount( amount ) => "Invalid resource amount: " + TUI.errorHighlight( amount ) + "!"
      case InvalidTradeResources( give, get ) =>
        "Invalid trade resources: " + TUI.errorHighlight( give.title ) + " <-> " + TUI.errorHighlight( get.title ) + "!"
      case InvalidDevCard( devCard ) => "Invalid dev card: [" + TUI.errorHighlight( devCard ) + "]!"
      case InsufficientDevCards( devCard ) => "Insufficient dev cards of " + TUI.errorHighlight( devCard.title ) + "!"
      case AlreadyUsedDevCardInTurn => "You already used a development card in this turn!"
      case DevCardDrawnInTurn( devCard ) =>
        "You've drawn this development card (" + TUI.errorHighlight( devCard.title ) + ") in this turn, you can use it in your next turn."
      case InsufficientBankResources => "Bank has insufficient resources!"
      case InconsistentData => "Internal problem, please try again."
      case DevStackIsEmpty => "Development card stack is empty!"
      case PlayerNameAlreadyExists( name ) => "Player with name: [" + TUI.errorHighlight( name ) + "] already exists!"
      case PlayerNameEmpty => "Player name can't be empty!"
      case PlayerNameTooLong( name ) =>
        "Player name [" + TUI.errorHighlight( name ) + "] is too long, maximum " + Game.maxPlayerNameLength + " characters!"
      case PlayerColorIsAlreadyInUse( playerColor ) =>
        "Player color " + TUI.colorOf( playerColor ) + playerColor.name + TUI.reset + " is already in use!"
      case InvalidPlayerID( id ) => "Invalid player id: [" + TUI.errorHighlight( id ) + "]!"
      case InvalidPlayer( playerID ) => "Invalid player with id: " + TUI.errorHighlight( playerID ) + "!"
      case NothingToUndo => "Nothing to undo!"
      case NothingToRedo => "Nothing to redo!"
      case e:ControllerError => e
      case t:Throwable => t + ": " + t.getMessage
    } )
    if ( actionInfo.isDefined ) TUI.action( actionInfo.get )
  }
}
