package de.htwg.se.catan.aview.gui

import javafx.beans.value.{ ChangeListener, ObservableValue }
import javafx.scene.control.TextArea
import javafx.scene.layout.Region
import scalafx.scene.control.Button
import scalafx.scene.layout.{ AnchorPane, BorderPane, GridPane, HBox, Priority, VBox }
import scalafx.stage.FileChooser

/**
 * @author Vincent76;
 */
class InfoPane( gui:GUI ) extends BorderPane {
  style = "-fx-border-color: #353535; -fx-border-width: 2 0 0 0"
  minHeight = 50
  vgrow = Priority.Always
  val undoButton:Button = new Button( "<-" ) {
    vgrow = Priority.Always
    onAction = _ => gui.controller.undoAction()
  }
  val redoButton:Button = new Button( "->" ) {
    vgrow = Priority.Always
    onAction = _ => gui.controller.redoAction()
  }
  val saveButton:Button = new Button( "S" ) {
    vgrow = Priority.Always
    onAction = _ => gui.controller.saveGame()
  }
  val loadButton:Button = new Button( "L" ) {
    vgrow = Priority.Always
    onAction = _ => {
      val fileChooser = new FileChooser() {
        title = "Load savegame"
      }
      val file = fileChooser.showOpenDialog( gui.stage )
      if( file != null )
        gui.controller.loadGame( file.getAbsolutePath )
    }
  }
  right = new VBox {
    vgrow = Priority.Always
    children = List(
      new HBox {
        hgrow = Priority.Always
        children = (saveButton, loadButton).productIterator.toList.map( b => new AnchorPane {
          vgrow = Priority.Always
          hgrow = Priority.Always
          children = b.asInstanceOf[Button]
          AnchorPane.setAnchors( b.asInstanceOf[Button], 0, 0, 0, 0 )
        } )
      },
      new HBox {
        children = (undoButton, redoButton).productIterator.toList.map( b => new AnchorPane {
          vgrow = Priority.Always
          children = b.asInstanceOf[Button]
          AnchorPane.setAnchors( b.asInstanceOf[Button], 0, 0, 0, 0 )
        } )
      }
    )
  }
  val textArea:TextArea = new TextArea {
    setEditable( false )
    setPrefColumnCount( 1 )
    styleClass.add( "infoTextArea" )
    textProperty().addListener( new ChangeListener[String] {
      override def changed( observableValue:ObservableValue[_ <: String], t:String, t1:String ):Unit = setScrollTop( Double.MaxValue )
    } )
  }
  center = new scalafx.scene.control.TextArea( textArea ) {
    style = "-fx-font-family: monospace;" +
      "-fx-text-fill: white;" +
      "-fx-text-inner-color: white;" +
      "-fx-font-weight:bold;" +
      "-fx-font-size: 16;" +
      "-fx-focus-color: transparent;"
  }

  def showInfo( info:String ):Unit = {
    textArea.appendText( "\n" + info )
  }

  def setBackground( ):Unit = {
    val l = textArea.lookup( ".content" )
    if( l != null && l.isInstanceOf[Region] ) {
      l.asInstanceOf[Region].setBackground( GUIApp.woodBackground ) // "-fx-background-image: url( \"/wood_background.png\" );"
    }
  }

  def update( ):Unit = {
    undoButton.disable = !gui.controller.hasUndo
    redoButton.disable = !gui.controller.hasRedo
  }
}
