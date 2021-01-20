package de.htwg.se.settlers.ui.tui

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.Game.PlayerID
import de.htwg.se.settlers.model.state._
import de.htwg.se.settlers.model.{ PlacementPointNotEmpty, _ }
import de.htwg.se.settlers.ui.UI
import de.htwg.se.settlers.ui.tui.TUI.InvalidFormat
import de.htwg.se.settlers.ui.tui.command.{ ExitCommand, HelpCommand, RedoCommand, UndoCommand }
import de.htwg.se.settlers.ui.tui.tuistate._
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

  val resourcePattern:String = "(\\s*(" + Resources.get.map( r => regexIgnoreCase( r.s ) ).mkString( "|" ) + ")\\s*[1-9][0-9]*\\s*)"

  val resourcePatternInfo:String = "<" + Resources.get.map( _.s ).mkString( "|" ) + "> <amount>"

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

  def awaitKey( s:String = "Press Enter to proceed" ):Unit = {
    action( s )
    scala.io.StdIn.readLine()
  }

  def highlight( o:Any = "" ):String = Console.YELLOW + o + reset

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
    val length = if ( toLength >= 0 ) toLength else p.idName.length
    p.color.c.t + p.idName.toLength( length ) + reset
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

  def resourceString( resources:ResourceCards, prefix:String = "" ):String = {
    resources.filter( _._2 > 0 ).map( r => prefix + r._2 + " " + r._1.s ).mkString( ", " )
  }
}

class TUI( val controller:Controller ) extends UI {

  TUI.clear()
  TUI.outln( "Loading ..." )

  override def start( ):Unit = {
    show()
  }

  override def show( ):Unit = controller.game.state match {
    case state:TUIState =>
      TUI.clear()
      val gameDisplay = state.getGameDisplay
      if ( gameDisplay.isDefined )
        TUI.out( gameDisplay.get )
      //TUI.outln()
      val actionInfo = state.getActionInfo
      TUI.outln()
      TUI.action( actionInfo )
      val commandInput = CommandInput( scala.io.StdIn.readLine )
      val globalCommand = findGlobalCommand( commandInput )
      if ( globalCommand.isDefined ) {
        if ( commandInput.input.matches( globalCommand.get.inputPattern ) )
          globalCommand.get.action( commandInput, state )
        else
          state.onError( InvalidFormat( commandInput.input ) )
      } else {
        val inputPattern = state.inputPattern
        if ( inputPattern.isEmpty || commandInput.input.matches( inputPattern.get ) )
          state.action( commandInput )
        else
          state.onError( InvalidFormat( commandInput.input ) )
      }
    case _ =>
  }

  def findGlobalCommand( commandInput:CommandInput ):Option[CommandAction] = {
    if ( commandInput.command.isDefined )
      return TUI.commands.find( _.command == commandInput.command.get )
    Option.empty
  }

  override def onInfo( info:Info ):Unit = {
    info match {
      case BeginnerInfo( beginner, diceValues ) =>
        val nameLength = controller.game.players.map( _._2.idName.length ).max
        diceValues.foreach( d => if ( d._2 > 0 ) TUI.outln( TUI.displayName( controller.player( d._1 ), nameLength ) + "   " + d._2 ) )
        TUI.outln( "\n->\t" + TUI.displayName( controller.player( beginner ) ) + " begins.\n" )
      case info:DiceInfo => TUI.outln( info.dices._1 + " + " + info.dices._2 + " = " + ( info.dices._1 + info.dices._2 ) )
      case GatherInfo( dices, playerResources ) =>
        TUI.outln( dices._1 + " + " + dices._2 + " = " + ( dices._1 + dices._2 ) )
        playerResources.foreach( d => {
          TUI.outln( TUI.displayName( controller.player( d._1 ) ) + " " + TUI.resourceString( d._2, "+" ) )
        } )
      case GotResourcesInfo( pID, cards ) =>
        TUI.outln( TUI.displayName( controller.player( pID ) ) + "  " + TUI.resourceString( cards, "+" ) )
      case LostResourcesInfo( pID, cards ) =>
        TUI.outln( TUI.displayName( controller.player( pID ) ) + "  " + TUI.resourceString( cards, "-" ) )
      case ResourceChangeInfo( playerAdd, playerSub ) =>
        val nameLength = ( playerAdd.keys ++ playerSub.keys ).map( controller.player( _ ).idName.length ).max
        playerSub.foreach( d => TUI.outln( TUI.displayName( controller.player( d._1 ), nameLength ) + "  " + TUI.resourceString( d._2, "-" ) ) )
        playerAdd.foreach( d => TUI.outln( TUI.displayName( controller.player( d._1 ), nameLength ) + "  " + TUI.resourceString( d._2, "+" ) ) )
      case BankTradedInfo( _, give, get ) =>
        TUI.outln( "You  traded " + give._2 + " " + give._1.s + " for " + get._2 + " " + get._1.s + "." )
      case DrawnDevCardInfo( _, devCard ) =>
        TUI.outln( "Drawn: " + devCard.t + "\n" + devCard.desc )
      case InsufficientStructuresInfo( _, structure ) =>
        TUI.outln( "You don't have enough structures of " + structure.s + " to build more." )
      case NoPlacementPointsInfo( _, structure ) =>
        TUI.outln( "There aren't any more possible placement points for structure " + structure.s + " to build more." )
      case GameEndInfo( winner ) =>
        val p = controller.player( winner )
        TUI.out( TUI.displayName( p ) + " won with " + p.getVictoryPoints( controller.game ) + " victory points!" )
      case _ => return
    }
    TUI.outln()
    TUI.awaitKey()
  }

  override def onError( t:Throwable ):Unit = {
    TUI.outln()
    TUI.error( t match {
      case WrongState => "Unable in this state!"
      case InsufficientResources => "Insufficient resources for this action!"
      case TradePlayerInsufficientResources => "Trade player has insufficient resources for this action!"
      case InsufficientStructures( structure ) =>
        "Insufficient structures of " + TUI.highlight( structure.s ) + " for this action!"
      case NonExistentPlacementPoint( id ) => "Placement point " + TUI.highlight( id ) + " does not exists!"
      case PlacementPointNotEmpty( id ) => "Placement point " + TUI.highlight( id ) + " is not empty!"
      case NoAdjacentStructure => "Player has no adjacent structure!"
      case TooCloseToBuilding( id ) => "Placement point " + TUI.highlight( id ) + " is too close to another building!"
      case NoConnectedStructures( id ) => "No connected structures on placement point " + TUI.highlight( id ) + "!"
      case SettlementRequired( id ) =>
        "You need a settlement on placement point " + TUI.highlight( id ) + " to build a city!"
      case InvalidPlacementPoint( id ) => "Invalid placement point " + TUI.highlight( id ) + "!"
      case NotEnoughPlayers => "Minimum " + TUI.highlight( Game.minPlayers ) + " players required!"
      case InvalidPlayerColor( color ) => "Invalid player color: " + TUI.highlight( "[" + color + "]" ) + "!"
      case RobberOnlyOnWater => "Robber can only be places on land!"
      case NoPlacementPoints( structure ) =>
        "No available placement points for structure " + TUI.highlight( structure.s ) + "!"
      case InvalidResourceAmount( amount ) => "Invalid resource amount: " + TUI.highlight( amount ) + "!"
      case InvalidTradeResources( give, get ) =>
        "Invalid trade resources: " + TUI.highlight( give.s ) + " <-> " + TUI.highlight( get.s ) + "!"
      case InvalidDevCard( devCard ) => "Invalid dev card: " + TUI.highlight( "[" + devCard + "]" ) + "!"
      case InsufficientDevCards( devCard ) => "Insufficient dev cards of " + TUI.highlight( devCard.t ) + "!"
      case AlreadyUsedDevCardInTurn => "You already used a development card in this turn!"
      case DevCardDrawnInTurn( devCard ) =>
        "You've drawn this development card (" + TUI.highlight( devCard.t ) + ") in this turn, you can use it in your next turn."
      case InsufficientBankResources( r:Resource ) => "Bank has insufficient resources of " + TUI.highlight( r.s ) + "!"
      case InconsistentData => "Internal problem, please try again."
      case DevStackIsEmpty => "Development card stack is empty!"
      case PlayerNameAlreadyExists( name ) => "Player with name: " + TUI.highlight( "[" + name + "]" ) + " already exists!"
      case PlayerColorIsAlreadyInUse( playerColor ) =>
        "Player color " + playerColor.c.t + playerColor.name + TUI.reset + " is already in use!"
      case InvalidPlayerID( id ) => "Invalid player id: " + TUI.highlight( "[" + id + "]" ) + "!"
      case InvalidPlayer( playerID ) => "Invalid player with id: " + TUI.highlight( playerID ) + "!"
      case NothingToUndo => "Nothing to undo!"
      case NothingToRedo => "Nothing to redo!"
      case e:ControllerError => e
      case t:Throwable => t + ": " + t.getMessage
    } )
    TUI.outln()
    TUI.awaitKey()
    show()
  }


  override def getInitState:InitState =
    new InitTUIState( controller )

  override def getInitPlayerState:InitPlayerState =
    new InitPlayerTUIState( controller )

  override def getInitBeginnerState( diceValues:Map[PlayerID, Int], counter:Int ):InitBeginnerState =
    new InitBeginnerTUIState( diceValues, counter, controller )

  override def getBuildInitSettlementState:BuildInitSettlementState =
    new BuildInitSettlementTUIState( controller )

  override def getBuildInitRoadState( vID:Int ):BuildInitRoadState =
    new BuildInitRoadTUIState( vID, controller )

  override def getNextPlayerState:NextPlayerState =
    new NextPlayerTUIState( controller )

  override def getDiceState( dices:(Int, Int) ):DiceState =
    new DiceTUIState( dices, controller )

  override def getDropHandCardsState( pID:PlayerID, dropped:List[PlayerID] ):DropHandCardsState =
    new DropHandCardsTUIState( pID, dropped, controller )

  override def getRobberPlaceState( nextState:State ):RobberPlaceState =
    new RobberPlaceTUIState( nextState, controller )

  override def getRobberStealState( nextState:State ):RobberStealState =
    new RobberStealTUIState( nextState, controller )

  override def getActionState:ActionState =
    new ActionTUIState( controller )

  override def getBuildState( structure:StructurePlacement ):BuildState =
    new BuildTUIState( structure, controller )

  override def getPlayerTradeState( pID:PlayerID, give:ResourceCards, get:ResourceCards, decisions:Map[PlayerID, Boolean] ):PlayerTradeState =
    new PlayerTradeTUIState( pID, give, get, decisions, controller )

  override def getPlayerTradeEndState( give:ResourceCards, get:ResourceCards, decisions:Map[PlayerID, Boolean] ):PlayerTradeEndState =
    new PlayerTradeEndTUIState( give, get, decisions, controller )

  override def getYearOfPlentyState( nextState:State ):YearOfPlentyState =
    new YearOfPlentyTUIState( nextState, controller )

  override def getDevRoadBuildingState( nextState:State, roads:Int ):DevRoadBuildingState =
    new DevRoadBuildingTUIState( nextState, roads, controller )

  override def getMonopolyState( nextState:State ):MonopolyState =
    new MonopolyTUIState( nextState, controller )
}
