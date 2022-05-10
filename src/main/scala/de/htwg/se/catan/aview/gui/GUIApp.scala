package de.htwg.se.catan.aview.gui

import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.aview.API
import de.htwg.se.catan.aview.gui.guistate.*
import de.htwg.se.catan.aview.gui.util.CustomDialog
import de.htwg.se.catan.controller.Controller
import de.htwg.se.catan.model.Card.*
import de.htwg.se.catan.model.*
import de.htwg.se.catan.model.Info.*
import de.htwg.se.catan.model.state.*
import de.htwg.se.catan.util.{ Observer, * }
import javafx.geometry.Side
import javafx.scene.layout.{ BackgroundImage, BackgroundPosition, BackgroundRepeat, BackgroundSize }
import scalafx.application.{ JFXApp3, Platform }
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.Image
import scalafx.scene.layout.Background
import scalafx.scene.paint.Color

/**
 * @author Vincent76;
 */

/*object GUIMain:
  def main( args:Array[String] ):Unit =
    CatanModule.init()
    GUIApp()*/


object GUIApp extends JFXApp3 with Observer:


/*class GUIApp(/* val controller:Controller */) extends Observer:
  val api:API = API()
  api.add( this )
  val gui:GUIWrap = new GUIWrap( this, api )
  JavaFXMain.
  gui.main( Array() )
  /*val thread:Thread = new Thread:
    override def run( ):Unit = 
      gui.main( Array() )
  thread.start()

  controller.add( this )*/


  def exit( ):Unit = System.exit( 0 )// gui.stopApp()

  override def onUpdate( game:Game, info:Option[Info] ):Unit =
    gui.update( game )
    if info.isDefined then
      onInfo( info.get )

  override def onInfo( info:Info ):Unit = gui.showInfoDialog( info )

  override def onError( t:Throwable ):Unit = gui.showError( t )*/

  val api:API = API()
  api.add( this )
  var gui:Option[GUI] = None

  override def start( ):Unit = try {
    CatanModule.init()
    api.rawGet[Game]( "game", game => Platform.runLater {
      gui = Some( GUI( api, game ) )
      stage = gui.get
      update( game )
    }, t => {
      print( t )
      stopApp()
    } )
  } catch {
    case t:Throwable => print( t )
  }

  override def stopApp( ):Unit = System.exit( 0 )

  override def onUpdate( game:Game, info:Option[Info] ):Unit =
    update( game )
    if info.isDefined then
      onInfo( info.get )

  override def onInfo( info:Info ):Unit = showInfoDialog( info )

  override def onError( t:Throwable ):Unit = showError( t )

  def update( game:Game/*, state:Option[GUIState] = None*/ ):Unit = gui match
    case Some( gui ) => gui.update( game )
    case None =>

  def showInfo( info:String ):Unit = gui match
    case Some( gui ) => gui.showInfo( info )
    case None =>

  def showInfoDialog( title:String, text:Option[String] = None, centered:Boolean = false ):Unit = gui match
    case Some( gui ) => gui.showInfoDialog( title, text, centered )
    case None =>

  def showInfoDialog( info:Info ):Unit = gui match
    case Some( gui ) => gui.showInfoDialog( info )
    case None =>

  def showErrorDialog( title:String, text:Option[String] = None, centered:Boolean = false ):Unit = gui match
    case Some( gui ) => gui.showErrorDialog( title, text, centered )
    case None =>

  def showDialog( dialog:CustomDialog ):Unit = gui match
    case Some( gui ) => gui.showDialog( dialog )
    case None =>

  def showError( t:Throwable ):Unit = gui match
    case Some( gui ) => Platform.runLater {
      new Alert( AlertType.Warning ):
        initOwner( stage )
        headerText = gui.getError( t )
      .showAndWait()
    }
    case None =>