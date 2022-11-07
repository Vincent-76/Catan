package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.CatanModule
import com.aimit.htwg.catan.controller.Controller
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO
import com.aimit.htwg.catan.util.RichString

import java.text.SimpleDateFormat
import java.util.Calendar
import scala.reflect.io.File

object FileIO extends ObjectComponent[FileIO] {
  def load( path:String ):(FileIO, (Game, List[Command], List[Command])) = {
    val extension = path.substring( path.lastIndexOf( "." ) + 1 )
    impls.find( _.extension ^= extension ) match {
      case Some( impl ) => (impl, impl.load( path ))
      case _ => throw new NotImplementedError( "Loader for extension: '" + extension + "'!" )
    }
  }
}

abstract class FileIO( val extension:String ) extends ComponentImpl {
  override def init():Unit = FileIO.addImpl( this )

  def getDefaultFileName:String = {
    File( CatanModule.savegamePath ).createDirectory().path + File.separator +
    "Catan_" + new SimpleDateFormat( "YYYY-MM-dd_HH.mm.ss" )
      .format( Calendar.getInstance().getTime ) + "_savegame"
  }

  def getFinalFileName( fileName:String ) = s"$fileName.$extension"

  def load( path:String ):(Game, List[Command], List[Command])

  def save( game:Game, undoStack:List[Command], redoStack:List[Command], fileName:String = getDefaultFileName ):String
}
