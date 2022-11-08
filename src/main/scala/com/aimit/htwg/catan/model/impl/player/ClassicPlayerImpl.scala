package com.aimit.htwg.catan.model.impl.player

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import com.aimit.htwg.catan.model.Card._
import com.aimit.htwg.catan.model._
import com.aimit.htwg.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import com.aimit.htwg.catan.model.impl.fileio.XMLFileIO.{ XMLMap, XMLNode, XMLNodeSeq, XMLSequence }
import com.aimit.htwg.catan.util._
import play.api.libs.json.{ JsValue, Json }

import scala.util.{ Failure, Random, Success, Try }
import scala.xml.Node

object ClassicPlayerImpl extends PlayerImpl( "ClassicPlayerImpl" ) {
  def fromXML( node:Node ):ClassicPlayerImpl = ClassicPlayerImpl(
    idVal = PlayerID.fromXML( node.childOf( "id" ) ),
    colorVal = PlayerColor.of( ( node \ "@color" ).content ).get,
    nameVal = ( node \ "@name" ).content,
    resourcesVal = ResourceCards.fromXML( node.childOf( "resources" ) ),
    devCardsVal = node.childOf( "devCards" ).asVector( n => DevelopmentCard.of( n.content ).get ),
    usedDevCards = node.childOf( "usedDevCards" ).asVector( n => DevelopmentCard.of( n.content ).get ),
    victoryPointsVal = ( node \ "@victoryPoints" ).content.toInt,
    structures = node.childOf( "structures" ).asMap( n => StructurePlacement.of( n.content ).get, _.content.toInt )
  )

  def fromJson( json:JsValue ):ClassicPlayerImpl = ClassicPlayerImpl(
    idVal = ( json \ "id" ).as[PlayerID],
    colorVal = ( json \ "color" ).as[PlayerColor],
    nameVal = ( json \ "name" ).as[String],
    resourcesVal = ( json \ "resources" ).as[ResourceCards],
    devCardsVal = ( json \ "devCards" ).as[Vector[DevelopmentCard]],
    usedDevCards = ( json \ "usedDevCards" ).as[Vector[DevelopmentCard]],
    victoryPointsVal = ( json \ "victoryPoints" ).as[Int],
    structures = ( json \ "structures" ).asMap[StructurePlacement, Int]
  )
}

case class ClassicPlayerImpl( idVal:PlayerID,
                              colorVal:PlayerColor,
                              nameVal:String,
                              resourcesVal:ResourceCards = Card.getResourceCards( 0 ),
                              devCardsVal:Vector[DevelopmentCard] = Vector.empty,
                              usedDevCards:Vector[DevelopmentCard] = Vector.empty,
                              victoryPointsVal:Int = 0,
                              structures:Map[StructurePlacement, Int] = StructurePlacement.impls.map( p => (p, p.available) ).toMap
                            ) extends Player {

  @Inject
  def this( @Assisted id:PlayerID, @Assisted color:PlayerColor, @Assisted name:String ) = this(
    idVal = id,
    colorVal = color,
    nameVal = name
  )


  def toXML:Node = <ClassicPlayerImpl color={ colorVal.name } name={ nameVal } victoryPoints={ victoryPointsVal.toString }>
    <id>{ idVal.toXML }</id>
    <resources>{ resourcesVal.toXML( _.name, _.toString ) }</resources>
    <devCards>{ devCardsVal.toXML( _.name ) }</devCards>
    <usedDevCards>{ usedDevCards.toXML( _.name ) }</usedDevCards>
    <structures>{ structures.toXML( _.name, _.toString ) }</structures>
  </ClassicPlayerImpl>.copy( label = ClassicPlayerImpl.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( ClassicPlayerImpl.name ),
    "id" -> Json.toJson( idVal ),
    "color" -> Json.toJson( colorVal ),
    "name" -> Json.toJson( nameVal ),
    "resources" -> Json.toJson( resourcesVal ),
    "devCards" -> Json.toJson( devCardsVal ),
    "usedDevCards" -> Json.toJson( usedDevCards ),
    "victoryPoints" -> Json.toJson( victoryPointsVal ),
    "structures" -> Json.toJson( structures )
  )


  def id:PlayerID = idVal
  def name:String = nameVal
  def resources:ResourceCards = resourcesVal
  def color:PlayerColor = colorVal
  def victoryPoints:Int = victoryPointsVal
  def devCards:Vector[DevelopmentCard] = devCardsVal

  def hasResources( resources:ResourceCards ):Boolean = resourcesVal.has( resources )

  def resourceAmount:Int = resourcesVal.amount
  def resourceAmount( resource:Resource ):Int = resourcesVal.getOrElse( resource, 0 )

  def removeResourceCard( resource:Resource, amount:Int = 1 ):Try[ClassicPlayerImpl] = resourcesVal.subtract( resource, amount ) match {
    case Success( newResources ) => Success( copy( resourcesVal = newResources ) )
    case Failure( e ) => Failure( e )
  }

  def removeResourceCards( cards:ResourceCards ):Try[ClassicPlayerImpl] = resourcesVal.subtract( cards ) match {
    case Success( newResources ) => Success( copy( resourcesVal = newResources ) )
    case Failure( e ) => Failure( e )
  }

  def addResourceCard( resource:Resource, amount:Int = 1 ):ClassicPlayerImpl = copy( resourcesVal = resourcesVal.add( resource, amount ) )

  def addResourceCards( cards:ResourceCards ):ClassicPlayerImpl = copy( resourcesVal = resourcesVal.add( cards ) )

  def trade( get:ResourceCards, give:ResourceCards ):Try[ClassicPlayerImpl] = addResourceCards( get ).removeResourceCards( give )

  def addDevCard( card:DevelopmentCard, removeFromUsed:Boolean = false ):ClassicPlayerImpl = if( removeFromUsed )
    copy( devCardsVal = devCardsVal :+ card, usedDevCards = usedDevCards.removed( card ).toVector )
  else copy( devCardsVal = devCardsVal :+ card )

  def removeLastDevCard( ):ClassicPlayerImpl = copy( devCardsVal = devCardsVal.init )

  def usedDevCards( devCard:DevelopmentCard ):Int = usedDevCards.count( _ == devCard )

  def addVictoryPoint( ):ClassicPlayerImpl = copy( victoryPointsVal = victoryPointsVal + 1 )

  def hasStructure( structure:StructurePlacement ):Boolean = structures.getOrElse( structure, 0 ) > 0

  def getStructure( structure:StructurePlacement ):Try[ClassicPlayerImpl] = {
    val available = structures.getOrElse( structure, 0 )
    if( available > 0 ) {
      val newStructures = if( structure.replaces.isDefined )
        structures.updated( structure.replaces.get, structures( structure.replaces.get ) + 1 )
      else structures
      Success( copy( structures = newStructures.updated( structure, available - 1 ) ) )
    } else
      Failure( InsufficientStructures( structure ) )
  }

  def addStructure( structure:StructurePlacement ):ClassicPlayerImpl = {
    val newStructures = if( structure.replaces.isDefined )
      structures.updated( structure.replaces.get, structures.getOrElse( structure.replaces.get, 0 ) - 1 )
    else structures
    copy(
      structures = newStructures.updated( structure, newStructures.getOrElse( structure, 0 ) + 1 )
    )
  }

  def randomHandResource( ):Option[Resource] = Random.element( resources.flatMap( d => ( 0 until d._2 ).map( _ => d._1 ) ).toSeq )

  def useDevCard( devCard:DevelopmentCard ):Try[ClassicPlayerImpl] = {
    val index = devCardsVal.indexOf( devCard )
    if( index >= 0 )
      Success( copy(
        devCardsVal = devCardsVal.removeAt( index ),
        usedDevCards = usedDevCards :+ devCard
      ) )
    else Failure( InsufficientDevCards( devCard ) )
  }
}
