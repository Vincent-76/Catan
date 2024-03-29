package com.aimit.htwg.catan.model.commands

import com.aimit.htwg.catan.model.Command.CommandSuccess
import com.aimit.htwg.catan.model.state.{ InitPlayerState, InitState }
import com.aimit.htwg.catan.model.{ Command, CommandImpl, Game }
import play.api.libs.json.{ JsValue, Json }

import scala.util.Try
import scala.xml.Node

/**
 * @author Vincent76;
 */

object InitGameCommand extends CommandImpl( "InitGameCommand" ) {
  override def fromXML( node:Node ):InitGameCommand = InitGameCommand()

  override def fromJson( json:JsValue ):InitGameCommand = InitGameCommand()
}

case class InitGameCommand() extends Command {

  def toXML:Node = <InitGameCommand />.copy( label = InitGameCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( InitGameCommand.name )
  )

  def doStep( game:Game ):Try[CommandSuccess] = success(
    game.setState( InitPlayerState() )
  )

  def undoStep( game:Game ):Game = game.setState( InitState() )
}
