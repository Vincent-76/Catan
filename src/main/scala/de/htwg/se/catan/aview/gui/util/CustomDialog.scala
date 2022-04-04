package de.htwg.se.catan.aview.gui.util

import de.htwg.se.catan.aview.gui.{ GUIApp, GUI }
import scalafx.geometry.Insets
import scalafx.scene.control.{ ButtonType, DialogPane }

/**
 * @author Vincent76;
 */
class CustomDialog( gui:GUI,
                    val title:String = "",
                    buttonType:ButtonType = ButtonType.OK ) extends DialogPane:


  padding = Insets( 0, 8, 0, 8 )
  buttonTypes += buttonType

  def close( ):Unit = scene.value.getWindow.hide()

  def show( ):Unit = gui.showDialog( this )