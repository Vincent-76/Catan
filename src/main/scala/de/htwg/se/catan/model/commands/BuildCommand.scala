package de.htwg.se.catan.model.commands

import de.htwg.se.catan.model.Command.CommandSuccess
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq }
import de.htwg.se.catan.model.impl.placement.{ CityPlacement, RoadPlacement, SettlementPlacement }
import de.htwg.se.catan.model.state.{ ActionState, BuildState }
import de.htwg.se.catan.model.{ BonusCard, _ }
import de.htwg.se.catan.model.info.BuiltInfo
import de.htwg.se.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Success, Try }
import scala.xml.Node

/**
 * @author Vincent76;
 */

object BuildCommand extends CommandImpl( "BuildCommand" ):
  override def fromXML( node:Node ):BuildCommand = BuildCommand(
    id = ( node \ "@id" ).content.toInt,
    state = BuildState.fromXML( node.childOf( "state" ) )
  )

  override def fromJson( json:JsValue ):BuildCommand = BuildCommand(
    id = ( json \ "id" ).as[Int],
    state = BuildState.fromJson( ( json \ "state" ).get )
  )


case class BuildCommand( id:Int, state:BuildState ) extends Command:

  def toXML:Node = <BuildCommand id={ id.toString }>
    <state>{ state.toXML }</state>
  </BuildCommand>.copy( label = BuildCommand.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( BuildCommand.name ),
    "id" -> Json.toJson( id ),
    "state" -> state.toJson
  )

  var actualBonusCards:Option[Map[BonusCard, Option[(PlayerID, Int)]]] = None

  override def doStep( game:Game ):Try[CommandSuccess] =
    state.structure.build( game, game.onTurn, id ) match
      case Success( newGame ) =>
        actualBonusCards = Some( game.bonusCards )
        success(
          newGame.setState( ActionState() ),
          Some( BuiltInfo( state.structure, id ) )
        )
      case f => f.rethrow

  override def undoStep( game:Game ):Game =
    val newGameField = state.structure match
      case RoadPlacement => game.gameField.update( game.gameField.findEdge( id ).get.setRoad( None ) )
      case SettlementPlacement => game.gameField.update( game.gameField.findVertex( id ).get.setBuilding( None ) )
      case CityPlacement => game.gameField.update( game.gameField.findVertex( id ).get.setBuilding( Some( Settlement( game.onTurn ) ) ) )
      //case _ => game.gameField
    game.setState( state )
      .setGameField( newGameField )
      .setBonusCards( actualBonusCards.get )
      .updatePlayer( game.player.addStructure( state.structure ) )


  //override def toString:String = getClass.getSimpleName + ": ID[" + id + "], " + state
