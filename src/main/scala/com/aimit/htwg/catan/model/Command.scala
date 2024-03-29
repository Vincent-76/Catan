package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.model.Command.CommandSuccess
import com.aimit.htwg.catan.model.impl.fileio.{ JsonSerializable, XMLSerializable }
import play.api.libs.json.{ JsSuccess, JsValue, Reads, Writes }

import scala.util.{ Success, Try }

/**
 * @author Vincent76;
 */

abstract class CommandImpl( name:String ) extends DeserializerComponentImpl[Command]( name ) {
  override def init():Unit = Command.addImpl( this )
}

object Command extends ClassComponent[Command, CommandImpl] {
  type CommandSuccess = (Game, Option[Info])

  implicit val stateWrites:Writes[Command] = ( o:Command ) => o.toJson
  implicit val stateReads:Reads[Command] = ( json:JsValue ) => JsSuccess( fromJson( json ) )
}

trait Command extends XMLSerializable with JsonSerializable {

  def success( game:Game, info:Option[Info] = None ):Success[CommandSuccess] =
    Success( (game, info) )

  def doStep( game:Game ):Try[CommandSuccess]

  def undoStep( game:Game ):Game
}
