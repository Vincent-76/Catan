package de.htwg.se.catan.model

import de.htwg.se.catan.model.impl.fileio.{ JsonSerializable, XMLSerializable }
import play.api.libs.json.{ JsSuccess, JsValue, Json, Reads, Writes }

/**
 * @author Vincent76;
 */
/*enum Info:
  given infoWrites:Writes[Info] = ( info:Info ) => Json.obj(
    "ordinal" -> Json.toJson( info.ordinal ),
    "data" -> info match
      case i:Info.GatherInfo => Json.obj(
        "dices" -> Json.toJson( dices ),
        "playerResources" -> Json.toJson( playerResources )
      )
  )

  case DiceInfo( dices:(Int, Int) ) extends Info 
  case GatherInfo( dices:(Int, Int), playerResources:Map[PlayerID, ResourceCards] ) extends Info  
  case GotResourcesInfo( pID:PlayerID, cards:ResourceCards ) extends Info  
  case LostResourcesInfo( pID:PlayerID, cards:ResourceCards ) extends Info  
  case ResourceChangeInfo( playerAdd:Map[PlayerID, ResourceCards], playerSub:Map[PlayerID, ResourceCards] ) extends Info  
  case BuiltInfo( structure:StructurePlacement, id:Int ) extends Info  
  case BankTradedInfo( pID:PlayerID, give:ResourceCards, get:ResourceCards ) extends Info
  case DrawnDevCardInfo( pID:PlayerID, devCard:DevelopmentCard ) extends Info
  case InsufficientStructuresInfo( pID:PlayerID, structure:StructurePlacement ) extends Info
  case NoPlacementPointsInfo( pID:PlayerID, structure:StructurePlacement ) extends Info
  case GameEndInfo( winner:PlayerID ) extends Info
  case GameSavedInfo( path:String ) extends Info
  case GameLoadedInfo( path:String ) extends Info*/


abstract class InfoImpl( name:String ) extends DeserializerComponentImpl[Info]( name ):
  override def init():Unit = Info.addImpl( this )


object Info extends ClassComponent[Info, InfoImpl]:
  given stateWrites:Writes[Info] = ( o:Info ) => o.toJson
  given stateReads:Reads[Info] = ( json:JsValue ) => JsSuccess( fromJson( json ) )


trait Info extends XMLSerializable with JsonSerializable