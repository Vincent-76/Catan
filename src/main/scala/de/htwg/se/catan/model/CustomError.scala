package de.htwg.se.catan.model

import de.htwg.se.catan.model.impl.fileio.{ JsonSerializable, XMLSerializable }
import play.api.libs.json.{ JsSuccess, JsValue, Reads, Writes }

/**
 * @author Vincent76;
 */

abstract class CustomErrorImpl( name:String ) extends DeserializerComponentImpl[CustomError]( name ):
  override def init():Unit = CustomError.addImpl( this )

object CustomError extends ClassComponent[CustomError, CustomErrorImpl]:
  given errorWrites:Writes[CustomError] = ( o:CustomError ) => o.toJson
  given errorReads:Reads[CustomError] = ( json:JsValue ) => JsSuccess( fromJson( json ) )



abstract class CustomError extends Throwable with XMLSerializable with JsonSerializable


/*case object Fail extends CustomError

case object WrongState extends CustomError

case object InsufficientResources extends CustomError

case object UniqueBeginnerExists extends CustomError

case object NoUniqueBeginner extends CustomError

case object TradePlayerInsufficientResources extends CustomError

case class InsufficientStructures( structure:StructurePlacement ) extends CustomError

case class NonExistentPlacementPoint( id:Int ) extends CustomError

case class UnavailableStructure( structure:StructurePlacement ) extends CustomError

case class PlacementPointNotEmpty( id:Int ) extends CustomError

case object NoAdjacentStructure extends CustomError

case class TooCloseToBuilding( id:Int ) extends CustomError

case class NoConnectedStructures( id:Int ) extends CustomError

case class SettlementRequired( id:Int ) extends CustomError

case class InvalidPlacementPoint( id:Int ) extends CustomError

case object NotEnoughPlayers extends CustomError

case class InvalidPlayerColor( color:String ) extends CustomError

case object RobberOnlyOnLand extends CustomError

case class NoPlacementPoints( structure:StructurePlacement ) extends CustomError

case class InvalidResourceAmount( amount:Int ) extends CustomError

//case class InvalidTradeResources( give:Resource, get:Resource ) extends CustomError

case class InvalidDevCard( devCard:String ) extends CustomError

case class InsufficientDevCards( devCard:DevelopmentCard ) extends CustomError

case object AlreadyUsedDevCardInTurn extends CustomError

case class DevCardDrawnInTurn( devCard:DevelopmentCard ) extends CustomError

case object InsufficientBankResources extends CustomError

case object InconsistentData extends CustomError

case object DevStackIsEmpty extends CustomError

case object PlayerNameEmpty extends CustomError

case class PlayerNameAlreadyExists( name:String ) extends CustomError

case class PlayerNameTooLong( name:String ) extends CustomError

case class PlayerColorIsAlreadyInUse( playerColor:PlayerColor ) extends CustomError

case class InvalidPlayerID( id:Int ) extends CustomError

case class InvalidPlayer( pID:PlayerID ) extends CustomError

case object NothingToUndo extends CustomError

case object NothingToRedo extends CustomError*/