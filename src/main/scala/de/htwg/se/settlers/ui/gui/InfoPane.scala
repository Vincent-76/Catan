package de.htwg.se.settlers.ui.gui

import de.htwg.se.settlers.controller.Controller
import javafx.beans.value.{ ChangeListener, ObservableValue }
import javafx.scene.control.TextArea
import scalafx.scene.control.Button
import scalafx.scene.layout.{ AnchorPane, BorderPane, Priority, VBox }

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
  right = new VBox {
    vgrow = Priority.Always
    children = (undoButton, redoButton).productIterator.toList.map( b => new AnchorPane {
      vgrow = Priority.Always
      children = b.asInstanceOf[Button]
      AnchorPane.setAnchors( b.asInstanceOf[Button], 0, 0, 0, 0 )
    } )
  }
  val textArea:TextArea = new TextArea {
    style = "-fx-font-family: monospace"
    setEditable( false )
    setPrefColumnCount( 1 )
    textProperty().addListener( new ChangeListener[String] {
      override def changed( observableValue:ObservableValue[_ <: String], t:String, t1:String ):Unit = setScrollTop( Double.MaxValue )
    } )
  }
  center = new scalafx.scene.control.TextArea( textArea )


  def showInfo( info:String ):Unit = {
    textArea.appendText( "\n" + info )
  }

  def update( ):Unit = {
    undoButton.disable = !gui.controller.hasUndo
    redoButton.disable = !gui.controller.hasRedo
  }
}
