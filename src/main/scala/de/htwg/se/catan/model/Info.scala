package de.htwg.se.catan.model

import Card.ResourceCards

/**
 * @author Vincent76;
 */
enum Info:

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
  case GameLoadedInfo( path:String ) extends Info
