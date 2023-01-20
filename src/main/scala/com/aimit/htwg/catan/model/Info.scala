package com.aimit.htwg.catan.model

import Card.ResourceCards
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO.JsonMap
import com.aimit.htwg.catan.model.impl.fileio.JsonSerializable
import play.api.libs.json.{ JsValue, Json }

/**
 * @author Vincent76;
 */
abstract class Info( name:String, val exclusive:Boolean = true ) extends NamedComponentImpl( name ) with JsonSerializable

case class DiceInfo( dices:(Int, Int) ) extends Info( "DiceInfo", false ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "dices" -> Json.toJson( dices )
  )
}

case class GatherInfo( dices:(Int, Int), playerResources:Map[PlayerID, ResourceCards] ) extends Info( "GatherInfo", false ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "dices" -> Json.toJson( dices ),
    "playerResources" -> playerResources.toJsonC( Json.toJson( _ ), _.toJson )
  )
}

case class GotResourcesInfo( pID:PlayerID, cards:ResourceCards ) extends Info( "GotResourcesInfo", false ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "pID" -> Json.toJson( pID ),
    "cards" -> cards.toJson
  )
}

case class LostResourcesInfo( pID:PlayerID, cards:ResourceCards ) extends Info( "LostResourcesInfo", false ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "pID" -> Json.toJson( pID ),
    "cards" -> cards.toJson
  )
}

case class ResourceChangeInfo( playerAdd:Map[PlayerID, ResourceCards], playerSub:Map[PlayerID, ResourceCards] ) extends Info( "ResourceChangeInfo", false ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "playerAdd" -> playerAdd.toJsonC( Json.toJson( _ ), _.toJson ),
    "playerSub" -> playerSub.toJsonC( Json.toJson( _ ), _.toJson )
  )
}

case class BuiltInfo( structure:StructurePlacement, id:Int ) extends Info( "BuiltInfo", true ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "structure" -> Json.toJson( structure ),
    "id" -> Json.toJson( cID )
  )
}

case class BankTradedInfo( pID:PlayerID, give:ResourceCards, get:ResourceCards ) extends Info( "BankTradedInfo", true ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "pID" -> Json.toJson( pID ),
    "give" -> give.toJson,
    "get" -> get.toJson
  )
}

case class DrawnDevCardInfo( pID:PlayerID, devCard:DevelopmentCard ) extends Info( "DrawnDevCardInfo", true ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "pID" -> Json.toJson( pID ),
    "devCard" -> Json.toJson( devCard )
  )
}

case class InsufficientStructuresInfo( pID:PlayerID, structure:StructurePlacement ) extends Info( "InsufficientStructuresInfo", true ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "pID" -> Json.toJson( pID ),
    "structure" -> Json.toJson( structure )
  )
}

case class NoPlacementPointsInfo( pID:PlayerID, structure:StructurePlacement ) extends Info( "NoPlacementPointsInfo", true ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "pID" -> Json.toJson( pID ),
    "structure" -> Json.toJson( structure )
  )
}

case class GameEndInfo( winner:PlayerID ) extends Info( "GameEndInfo", false ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "winner" -> Json.toJson( winner )
  )
}

case class GameSavedInfo( path:String ) extends Info( "GameSavedInfo", true ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "path" -> Json.toJson( path )
  )
}

case class GameLoadedInfo( path:String ) extends Info( "GameLoadedInfo", false ) {
  override def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( name ),
    "path" -> Json.toJson( path )
  )
}
