package de.htwg.se.catan.model

import java.text.SimpleDateFormat
import java.util.Calendar

trait FileIO {
  def getFileName:String =
    "Catan_" + new SimpleDateFormat( "YYYY-MM-dd_HH.mm.ss" ).format( Calendar.getInstance().getTime ) + "_savegame"

  def load( path:String ):Game

  def save( game:Game ):String
}
