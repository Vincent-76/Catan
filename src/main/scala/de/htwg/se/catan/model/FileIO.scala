package de.htwg.se.catan.model

import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.model.impl.fileio.JsonFileIO
import de.htwg.se.catan.util.^=

import java.text.SimpleDateFormat
import java.util.Calendar
import scala.reflect.io.File

object FileIO extends ObjectComponent[FileIO]:
  def load( id:String ):(Game, List[Command], List[Command]) =
    val extension = if( id.forall( Character.isDigit ) )
      "slick"
    else id.substring( id.lastIndexOf( "." ) + 1 )
    impls.find( _.extension ^= extension ) match
      case Some( impl ) => impl.load( id )
      case _ => throw NotImplementedError( "Loader for extension: '" + extension + "'!" )


abstract class FileIO( val extension:String ) extends ComponentImpl:
  override def init():Unit = FileIO.addImpl( this )

  def getFileName:String =
    File( CatanModule.savegamePath ).createDirectory().path + File.separator +
    "Catan_" + SimpleDateFormat( "YYYY-MM-dd_HH.mm.ss" )
      .format( Calendar.getInstance().getTime ) + "_savegame." + extension

  def load( id:String ):(Game, List[Command], List[Command])

  def save( game:Game, undoStack:List[Command], redoStack:List[Command] ):String