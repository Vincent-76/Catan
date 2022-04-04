package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Command.CommandSuccess
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import de.htwg.se.catan.model.state.InitBeginnerState
import de.htwg.se.catan.model.{ Command, _ }
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Random, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object DiceOutBeginnerCommand extends CommandImpl( "DiceOutBeginnerCommand" ):
  override def fromXML( node:Node ):DiceOutBeginnerCommand = DiceOutBeginnerCommand(
    state = InitBeginnerState.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):DiceOutBeginnerCommand = DiceOutBeginnerCommand(
    state = InitBeginnerState.fromJson( ( json \ "state" ).get )
  )


case class DiceOutBeginnerCommand( state:InitBeginnerState ) extends Command:

  def toXML:Node = <DiceOutBeginnerCommand>
    <state>{ state.toXML }</state>
  </DiceOutBeginnerCommand>.copy( label = DiceOutBeginnerCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( DiceOutBeginnerCommand.name ),
    "state" -> state.toJson
  )

  override def doStep( game:Game ):Try[CommandSuccess] =
    if state.beginner.isDefined then
      Failure( UniqueBeginnerExists )
    else
      val values = if state.diceValues.isEmpty then
        val r = new Random( game.seed * 1000 )
        game.players.map( d => (d._1, game.rollDice( r )) )
      else
        val max = state.diceValues.maxBy( _._2 )._2
        val r = new Random( game.seed * state.counter * -1 )
        state.diceValues.map( d => (d._1, if d._2 < max then 0 else game.rollDice( r )) )
      val maxValue = values.maxBy( _._2 )
      val beginners = values.count( _._2 >= maxValue._2 )
      if beginners > 1 then
        success( game.setState( InitBeginnerState( None, values, state.counter + 1 ) ), None )
      else
        success( game.setState( InitBeginnerState( Some( maxValue._1 ), values ) ), None )

  override def undoStep( game:Game ):Game = game.setState( state )

  //override def toString:String = getClass.getSimpleName + ": " + state
