package de.htwg.se.catan.model

import de.htwg.se.catan.CatanModule

import java.text.SimpleDateFormat
import java.util.Calendar
import scala.reflect.io.File

trait FileIO {
  def getFileName:String = {
    File( CatanModule.savegamePath ).createDirectory().path + File.separator +
    "Catan_" + new SimpleDateFormat( "YYYY-MM-dd_HH.mm.ss" ).format( Calendar.getInstance().getTime ) + "_savegame"
  }

  def load( path:String ):Game

  def save( game:Game ):String
}
