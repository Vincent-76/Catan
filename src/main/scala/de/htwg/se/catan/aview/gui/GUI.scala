package de.htwg.se.catan.aview.gui

import de.htwg.se.catan.aview.API
import de.htwg.se.catan.aview.gui.GUIApp
import de.htwg.se.catan.aview.gui.gamefield.{ GameFieldCanvas, GameFieldPane, PlacementOverlay }
import de.htwg.se.catan.aview.gui.guistate.{ ActionGUIState, BuildGUIState, BuildInitRoadGUIState, BuildInitSettlementGUIState, DevRoadBuildingGUIState, DiceGUIState, DropHandCardsGUIState, InitBeginnerGUIState, InitGUIState, InitPlayerGUIState, MonopolyGUIState, NextPlayerGUIState, PlayerTradeEndGUIState, PlayerTradeGUIState, RobberPlaceGUIState, RobberStealGUIState }
import de.htwg.se.catan.aview.gui.util.CustomDialog
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.info.*
import de.htwg.se.catan.model.error.*
import de.htwg.se.catan.model._
import de.htwg.se.catan.model.state.{ ActionState, BuildInitRoadState, BuildInitSettlementState, BuildState, DevRoadBuildingState, DiceState, DropHandCardsState, InitBeginnerState, InitPlayerState, InitState, MonopolyState, NextPlayerState, PlayerTradeEndState, PlayerTradeState, RobberPlaceState, RobberStealState, YearOfPlentyState }
import de.htwg.se.catan.util.*
import de.htwg.se.catan.model.Card.*
import javafx.geometry.Side
import javafx.scene.input.{ KeyCode, KeyCodeCombination, KeyCombination }
import javafx.scene.layout.{ BackgroundImage, BackgroundPosition, BackgroundRepeat, BackgroundSize }
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.{ JFXApp3, Platform }
import scalafx.geometry.Pos
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.Image
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.text.{ Text, TextAlignment }
import scalafx.scene.{ Node, Scene }

/**
 * @author Vincent76;
 */

object GUI:
  val stoneBackground:Background = getBackground( "/stone_background.png" )
  val woodBackground:Background = getBackground( "/wood_background.png" )
  val resourceIcons:Map[Resource, Image] = Resource.impls.map( r => (r, new Image( "/resources/" + r.title.toLowerCase + ".png" )) ).toMap
  val devCardIcon:Image = new Image( "/resources/dev.png" )


  private def getBackground( url:String ):Background =
    val tile:Image = new Image( url )
    val backgroundPosition = new BackgroundPosition( Side.LEFT, 0, false, Side.TOP, 0, false )
    val backgroundImage = new BackgroundImage( tile, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, backgroundPosition,
      new BackgroundSize( 80, 80, false, false, false, false ) )
    new Background( new javafx.scene.layout.Background( backgroundImage ) )

  def colorOf( playerColor:PlayerColor ):Color = playerColor match
    case Green => Color.Green.brighter
    case Red => Color.Red
    case Yellow => Color.Yellow.darker
    case Blue => Color.CornflowerBlue

  def colorOf( f:FieldType ):Color = f match
    case Water => Color.DodgerBlue
    case Desert => Color.Wheat
    case Wood => Color.Sienna
    case Clay => Color.DarkSalmon
    case Sheep => Color.PaleGreen
    case Wheat => Color.Khaki
    case Ore => Color.DarkGray
    case _ => Color.Black

  def middleOf( p1:(Double, Double), p2:(Double, Double) ):(Double, Double) =
    val a = (p2._1 - p1._1, p2._2 - p1._2)
    (p1._1 + a._1 / 2, p1._2 + a._2 / 2)

  def middleOf( p1:(Double, Double), p2:(Double, Double), p3:(Double, Double) ):(Double, Double) =
    val a = (p2._1 - p1._1, p2._2 - p1._2)
    val m = (p1._1 + a._1 / 2, p1._2 + a._2 / 2)
    val r = Math.sqrt( Math.pow( a._1, 2 ) + Math.pow( a._2, 2 ) ) / (2 * Math.sqrt( 3 ))
    val v = (p3._1 - m._1, p3._2 - m._2)
    val vl = Math.sqrt( Math.pow( v._1, 2 ) + Math.pow( v._2, 2 ) )
    (m._1 + r / vl * v._1, m._2 + r / vl * v._2)

  implicit class RichColor( val color:Color ):
    def toHex:String = "#%02X%02X%02X".format(
      (color.red * 255).toInt,
      (color.green * 255).toInt,
      (color.blue * 255).toInt )

class GUI( val api:API, var game:Game ) extends PrimaryStage:
  var dialog:Option[CustomDialog] = None
  val gameStackPane:GameStackPane[_] = GameStackPane.get( game )
  val playerListPane:PlayerListPane = new PlayerListPane( this )
  val gameFieldPane:GameFieldPane = new GameFieldPane(
    GameFieldCanvas.get( game.gameField ),
    PlacementOverlay.get( game.availablePlacements )
  )
  val playerPane:PlayerPane = new PlayerPane( this )
  val actionPane:ActionPane = new ActionPane( this )
  val infoPane:InfoPane = new InfoPane( this )


  icons.add( new Image( "/catan_icon.png" ) )
  title = "Settlers of Catan"
  minWidth = 850
  minHeight = 650
  width.onChange( ( _, _, _ ) => gameFieldPane.center = null )
  height.onChange( ( _, _, _ ) => gameFieldPane.center = null )
  gameFieldPane.width.onChange( ( _, _, _ ) => gameFieldPane.updateAll( game ) )
  gameFieldPane.height.onChange( ( _, _, _ ) => gameFieldPane.updateAll( game ) )
  scene = new Scene( 850, 650 ):
    //val css = Source.fromResource( "style.css" ).mkString
    stylesheets.add( "style.css" ) // getClass.getResource( "/style.css" ).toExternalForm )
    root = new GridPane:
      style = "-fx-border-color: #353535; -fx-border-width: 2"
      rowConstraints = List(
        new RowConstraints:
          percentHeight = 90
        ,
        new RowConstraints:
          percentHeight = 10
      )
      columnConstraints = List( new ColumnConstraints:
        percentWidth = 100
      )
      add( new BorderPane:
        background = GUI.stoneBackground
        left = new VBox:
          style = "-fx-border-color: #353535; -fx-border-width: 0 2 0 0"
          minWidth = 160
          maxWidth = 160
          children = List(
            gameStackPane,
            playerListPane
          )
        center = CenterPane
        right = new VBox:
          style = "-fx-border-color: #353535; -fx-border-width: 0 0 0 2"
          minWidth = 160
          maxWidth = 160
          children = List(
            playerPane,
            actionPane
          )
      , 0, 0 )
      add( infoPane, 0, 1 )
    val undoKeys = new KeyCodeCombination( KeyCode.Z, KeyCombination.CONTROL_DOWN )
    val redoKeys = new KeyCodeCombination( KeyCode.Y, KeyCombination.CONTROL_DOWN )
    onKeyPressed = e =>
      if( undoKeys.`match`( e ) && api.hasUndo ) api.undoAction()
      else if( redoKeys.`match`( e ) && api.hasUndo ) api.redoAction()


  update( game )


  Platform.runLater {
    infoPane.setBackground()
  }
  /*def postInit():Unit = {
    infoPane.setBackground
  }*/


  def update( game:Game ):Unit = Platform.runLater {
    this.game = game
    if dialog.isDefined then
      dialog.get.close()
    gameStackPane.update( game )
    playerListPane.update( game )
    infoPane.update()
    getGUIState( game.state ) match
      case Some( guiState ) =>
        playerPane.update( game, guiState.playerDisplayed )
        actionPane.update( guiState.getActions )
        guiState.getDisplayState match
          case i:InitDisplayState =>
            val pane = i.getDisplayPane
            pane.background = GUI.stoneBackground
            //pane.style = pane.style.value + ";-fx-background-color: #FFFFFF"
            setCenter( pane )
          case s:FieldDisplayState =>
            s match
              case f:FieldInputDisplayState => gameFieldPane.interactionPane.setInput( f )
              case _ => gameFieldPane.interactionPane.resetInput()
            resetCenter()
            gameFieldPane.updateOverlay( game )
      case _ =>
  }

  object CenterPane extends StackPane:
    //minWidth = 450
    alignment = Pos.Center
    children = gameFieldPane

    def setBackground( ):Unit =
      //CenterPane.style = "-fx-background-color: " + GUI.colorOf( Water ).toHex
      val tile:Image = new Image( "/water_background.png" )
      val backgroundPosition = new BackgroundPosition( Side.LEFT, 0, false, Side.TOP, 0, false )
      val backgroundImage = new BackgroundImage( tile, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, backgroundPosition,
        new BackgroundSize( 300, 300, false, false, false, false ) )
      background = new Background( new javafx.scene.layout.Background( backgroundImage ) )

  def setCenter( node:Node ):Unit =
    CenterPane.children = node
    CenterPane.style = "-fx-background-color: white"

  def resetCenter( ):Unit =
    if !CenterPane.children.contains( gameFieldPane ) then
      CenterPane.children = gameFieldPane
      CenterPane.setBackground()
      gameFieldPane.updateAll( game )

  def showInfo( info:String ):Unit = Platform.runLater {
    infoPane.showInfo( info )
  }

  def showInfoDialog( info:Info ):Unit = info match
    case DiceInfo( dices ) =>
      showInfoDialog( s"${dices._1} + ${dices._2} = ${dices._1 + dices._2}" )
    case GatherInfo( dices, playerResources ) =>
      showInfo( playerResources.map( d => game.player( d._1 ).name.toLength( game.maxPlayerNameLength ) + "  " + d._2.toString( "+" ) ).mkString( "\n" ) )
      showInfoDialog( s"${dices._1} + ${dices._2} = ${dices._1 + dices._2}", Some(
        playerResources.toList.sortBy( _._1.id ).map( d => {
          game.player( d._1 ).name.toLength( game.maxPlayerNameLength ) + "  " + d._2.toString( "+" )
        } ).mkString( "\n" )
      ) )
    case GotResourcesInfo( pID, cards ) =>
      showInfo( game.player( pID ).name.toLength( game.maxPlayerNameLength ) + "  " + cards.toString( "+" ) )
    case LostResourcesInfo( pID, cards ) =>
      showInfo( game.player( pID ).name.toLength( game.maxPlayerNameLength ) + "  " + cards.toString( "-" ) )
    case ResourceChangeInfo( playerAdd, playerSub ) =>
      showInfo( (playerAdd.keys.toSet ++ playerSub.keys.toSet).toList.sortBy( _.id ).map( pID => {
        val lists = Nil ++
          (if( playerAdd.contains( pID ) ) List( playerAdd( pID ).toString( "+" ) ) else Nil) ++
          (if( playerSub.contains( pID ) ) List( playerSub( pID ).toString( "-" ) ) else Nil)
        game.player( pID ).name.toLength( game.maxPlayerNameLength ) + "  " + lists.mkString( ", " )
      } ).mkString( "\n" ) )
    case BankTradedInfo( pID, give, get ) =>
      showInfo( game.player( pID ).name.toLength( game.maxPlayerNameLength ) + "  " + give.toString( "" ) + "  <->  " + get.toString( "" ) )
    case DrawnDevCardInfo( _, devCard ) =>
      showInfoDialog( "Drawn: " + devCard.title, Some( devCard.desc ) )
    case InsufficientStructuresInfo( pID, structure ) =>
      showInfo( game.player( pID ).name + ": Insufficient structures of " + structure.title + " to build more." )
    case NoPlacementPointsInfo( pID, structure ) =>
      showInfo( game.player( pID ).name +
        ": There aren't any more possible placement points for structure " + structure.title + " to build more." )
    case GameEndInfo( winner ) =>
      val p = game.player( winner )
      showInfoDialog(
        "Game End",
        Some( p.name + " won with " + game.getPlayerVictoryPoints( p.id ) + " victory points!" ),
        centered = true
      )
    case GameSavedInfo( path ) => showInfoDialog( "Game saved", text = Some( path ) )
    case GameLoadedInfo( path ) => showInfoDialog( "Game loaded", text = Some( path ) )
    case _ =>

  def showInfoDialog( title:String, text:Option[String] = None, centered:Boolean = false ):Unit = Platform.runLater {
    getMessageDialog( "Info", title, text, centered ).show()
  }

  def showErrorDialog( title:String, text:Option[String] = None, centered:Boolean = false ):Unit = Platform.runLater {
    getMessageDialog( "Error", title, text, centered ).show()
  }

  def getMessageDialog( title:String, headerString:String, text:Option[String] = None, centered:Boolean = false ):CustomDialog =
    new CustomDialog( this, title ):
      headerText = headerString
      if text.isDefined then
        content = new Text( text.get ):
          wrappingWidth = 300
          if centered then
            textAlignment = TextAlignment.Center

  def showDialog( dialog:CustomDialog ):Unit =
    if this.dialog.isDefined then
      this.dialog.get.close()
    this.dialog = Some( dialog )
    new Alert( AlertType.None, dialog.title ):
      initOwner( GUIApp.stage )
      dialogPane = dialog
    .showAndWait()
    this.dialog = None

  def getGUIState( state:State ):Option[GUIState] = state match
    case s:InitState => Some( InitGUIState( this ) )
    case s:InitPlayerState => Some( InitPlayerGUIState( this ) )
    case s:InitBeginnerState => Some( InitBeginnerGUIState( s, this ) )
    case s:BuildInitSettlementState => Some( BuildInitSettlementGUIState( this ) )
    case s:BuildInitRoadState => Some( BuildInitRoadGUIState( s, this ) )
    case s:NextPlayerState => Some( NextPlayerGUIState( this ) )
    case s:DiceState => Some( DiceGUIState( this ) )
    case s:DropHandCardsState => Some( DropHandCardsGUIState( s, this ) )
    case s:RobberPlaceState => Some( RobberPlaceGUIState( this ) )
    case s:RobberStealState => Some( RobberStealGUIState( s, this ) )
    case s:ActionState => Some( ActionGUIState( this ) )
    case s:BuildState => Some( BuildGUIState( s, this ) )
    case s:PlayerTradeState => Some( PlayerTradeGUIState( s, this ) )
    case s:PlayerTradeEndState => Some( PlayerTradeEndGUIState( s, this ) )
    case s:YearOfPlentyState => Some( guistate.YearOfPlentyGUIState( this ) )
    case s:DevRoadBuildingState => Some( DevRoadBuildingGUIState( this ) )
    case s:MonopolyState => Some( MonopolyGUIState( this ) )
    case _ => None

  def getError( t:Throwable ):String = t match
      case WrongState => "Unable in this state!"
      case InsufficientResources => "Insufficient resources for this action!"
      case TradePlayerInsufficientResources => "Trade player has insufficient resources for this action!"
      case InsufficientStructures( structure ) =>
        "Insufficient structures of " + structure.title + " for this action!"
      case NonExistentPlacementPoint( id ) => "This placement point does not exists!"
      case PlacementPointNotEmpty( id ) => "This placement point is not empty!"
      case NoAdjacentStructure => "Player has no adjacent structure!"
      case TooCloseToBuilding( id ) => "This placement point is too close to another building!"
      case NoConnectedStructures( id ) => "No connected structures on this placement point!"
      case SettlementRequired( id ) =>
        "You need a settlement on this placement point to build a city!"
      case InvalidPlacementPoint( id ) => "Invalid placement point!"
      case NotEnoughPlayers => "Minimum " + game.minPlayers + " players required!"
      case InvalidPlayerColor( color ) => "Invalid player color: [" + color + "]!"
      case RobberOnlyOnLand => "Robber can only be placed on land!"
      case NoPlacementPoints( structure ) =>
        "No available placement points for structure " + structure.title + "!"
      case InvalidResourceAmount( amount ) => "Invalid resource amount: " + amount + "!"
      /*case InvalidTradeResources( give, get ) =>
        "Invalid trade resources: " + give.title + " <-> " + get.title + "!"*/
      case InvalidDevCard( devCard ) => "Invalid dev card: [" + devCard + "]!"
      case InsufficientDevCards( devCard ) => "Insufficient dev cards of " + devCard.title + "!"
      case AlreadyUsedDevCardInTurn => "You already used a development card in this turn!"
      case DevCardDrawnInTurn( devCard ) =>
        "You've drawn this development card (" + devCard.title + ") in this turn, you can use it in your next turn."
      case InsufficientBankResources => "Bank has insufficient resources!"
      case InconsistentData => "Internal problem, please try again."
      case DevStackIsEmpty => "Development card stack is empty!"
      case PlayerNameAlreadyExists( name ) => "Player with name: [" + name + "] already exists!"
      case PlayerNameEmpty => "Player name can't be empty!"
      case PlayerNameTooLong( name ) =>
        "Player name [" + name + "] is too long, maximum " + game.maxPlayerNameLength + " characters!"
      case PlayerColorIsAlreadyInUse( playerColor ) =>
        "Player color: [" + playerColor.title + "] is already in use!"
      case InvalidPlayerID( id ) => "Invalid player id: [" + id + "]!"
      case InvalidPlayer( playerID ) => "Invalid player with id: " + playerID + "!"
      case NothingToUndo => "Nothing to undo!"
      case NothingToRedo => "Nothing to redo!"
      case e:CustomError => "Unknown error!"
      case t:Throwable => s"$t: ${t.getMessage}"
