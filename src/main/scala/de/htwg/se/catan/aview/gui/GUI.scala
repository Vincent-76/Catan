package de.htwg.se.catan.aview.gui

import de.htwg.se.catan.aview.gui.gamefield.{ GameFieldCanvas, GameFieldPane, PlacementOverlay }
import de.htwg.se.catan.aview.gui.util.CustomDialog
import de.htwg.se.catan.controller.Controller
import javafx.geometry.Side
import javafx.scene.input.{ KeyCode, KeyCodeCombination, KeyCombination }
import javafx.scene.layout.{ BackgroundImage, BackgroundPosition, BackgroundRepeat, BackgroundSize }
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{ JFXApp, Platform }
import scalafx.geometry.Pos
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.Image
import scalafx.scene.layout._
import scalafx.scene.text.{ Text, TextAlignment }
import scalafx.scene.{ Node, Scene }

/**
 * @author Vincent76;
 */
class GUI( guiApp:GUIApp, val controller:Controller ) extends JFXApp {

  var dialog:Option[CustomDialog] = None
  val gameStackPane:GameStackPane[_] = GameStackPane.get( controller.game )
  val playerListPane:PlayerListPane = new PlayerListPane( this )
  val gameFieldPane:GameFieldPane = new GameFieldPane(
    GameFieldCanvas.get( controller.game.gameField ),
    PlacementOverlay.get( controller.game.availablePlacements )
  )
  val playerPane:PlayerPane = new PlayerPane( this )
  val actionPane:ActionPane = new ActionPane( this )
  val infoPane:InfoPane = new InfoPane( this )

  stage = new PrimaryStage {
    title = "Settlers of Catan"
    minWidth = 850
    minHeight = 650
    width.addListener( ( _, _, _ ) => gameFieldPane.center = null )
    height.addListener( ( _, _, _ ) => gameFieldPane.center = null )
    gameFieldPane.width.addListener( ( _, _, _ ) => gameFieldPane.updateAll( controller.game ) )
    gameFieldPane.height.addListener( ( _, _, _ ) => gameFieldPane.updateAll( controller.game ) )
    scene = new Scene( 850, 650 ) {
      //val css = Source.fromResource( "style.css" ).mkString
      stylesheets.add( "style.css" )// getClass.getResource( "/style.css" ).toExternalForm )
      root = new GridPane {
        style = "-fx-border-color: #353535; -fx-border-width: 2"
        rowConstraints = List(
          new RowConstraints {
            percentHeight = 90
          },
          new RowConstraints {
            percentHeight = 10
          }
        )
        columnConstraints = List( new ColumnConstraints {
          percentWidth = 100
        } )
        add( new BorderPane {
          background = GUIApp.stoneBackground
          left = new VBox {
            style = "-fx-border-color: #353535; -fx-border-width: 0 2 0 0"
            minWidth = 160
            maxWidth = 160
            children = List(
              gameStackPane,
              playerListPane
            )
          }
          center = CenterPane
          right = new VBox {
            style = "-fx-border-color: #353535; -fx-border-width: 0 0 0 2"
            minWidth = 160
            maxWidth = 160
            children = List(
              playerPane,
              actionPane
            )
          }
        }, 0, 0 )
        add( infoPane, 0, 1 )
      }
      val undoKeys = new KeyCodeCombination( KeyCode.Z, KeyCombination.CONTROL_DOWN )
      val redoKeys = new KeyCodeCombination( KeyCode.Y, KeyCombination.CONTROL_DOWN )
      onKeyPressed = e =>
        if ( undoKeys.`match`( e ) && controller.hasUndo ) controller.undoAction()
        else if ( redoKeys.`match`( e ) && controller.hasUndo ) controller.redoAction()
    }
  }


  guiApp.onUpdate( None )


  Platform.runLater {
    infoPane.setBackground()
  }
  /*def postInit():Unit = {
    infoPane.setBackground
  }*/


  def update( state:Option[GUIState] = None ):Unit = Platform.runLater {
    if ( dialog.isDefined )
      dialog.get.close()
    gameStackPane.update( controller.game )
    playerListPane.update( controller.game )
    infoPane.update()
    state match {
      case Some( guiState ) =>
        playerPane.update( controller.game, guiState.playerDisplayed )
        actionPane.update( guiState.getActions )
        guiState.getDisplayState match {
          case i:InitDisplayState =>
            val pane = i.getDisplayPane
            pane.background = GUIApp.stoneBackground
            //pane.style = pane.style.value + ";-fx-background-color: #FFFFFF"
            setCenter( pane )
          case s:FieldDisplayState =>
            s match {
              case f:FieldInputDisplayState => gameFieldPane.interactionPane.setInput( f )
              case _ => gameFieldPane.interactionPane.resetInput()
            }
            resetCenter()
            gameFieldPane.updateOverlay( controller.game )
        }
      case _ =>
    }
  }

  object CenterPane extends StackPane {
    //minWidth = 450
    alignment = Pos.Center
    children = gameFieldPane

    def setBackground():Unit = {
      //CenterPane.style = "-fx-background-color: " + GUIApp.colorOf( Water ).toHex
      val tile:Image = new Image("/water_background.png")
      val backgroundPosition = new BackgroundPosition( Side.LEFT, 0, false, Side.TOP, 0, false )
      val backgroundImage = new BackgroundImage( tile, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, backgroundPosition,
        new BackgroundSize( 300, 300, false, false, false, false ) )
      background = new Background( new javafx.scene.layout.Background( backgroundImage ) )
    }
  }

  def setCenter( node:Node ):Unit = {
    CenterPane.children = node
    CenterPane.style = "-fx-background-color: white"
  }

  def resetCenter( ):Unit = {
    if ( !CenterPane.children.contains( gameFieldPane ) ) {
      CenterPane.children = gameFieldPane
      CenterPane.setBackground()
      gameFieldPane.updateAll( controller.game )
    }
  }

  def showInfo( info:String ):Unit = Platform.runLater {
    infoPane.showInfo( info )
  }

  def showInfoDialog( title:String, text:Option[String] = None, centered:Boolean = false ):Unit = Platform.runLater {
    getMessageDialog( "Info", title, text, centered ).show()
  }

  def showErrorDialog( title:String, text:Option[String] = None, centered:Boolean = false ):Unit = Platform.runLater {
    getMessageDialog( "Error", title, text, centered ).show()
  }

  def getMessageDialog( title:String, headerString:String, text:Option[String] = None, centered:Boolean = false ):CustomDialog =
    new CustomDialog( this, title ) {
      headerText = headerString
      if ( text.isDefined )
        content = new Text( text.get ) {
          wrappingWidth = 300
          if ( centered )
            textAlignment = TextAlignment.Center
        }
    }

  def showDialog( dialog:CustomDialog ):Unit = {
    if ( this.dialog.isDefined )
      this.dialog.get.close()
    this.dialog = Some( dialog )
    new Alert( AlertType.None, dialog.title ) {
      initOwner( stage )
      dialogPane = dialog
    }.showAndWait()
    this.dialog = None
  }

  override def stopApp( ):Unit = System.exit( 0 )
}
