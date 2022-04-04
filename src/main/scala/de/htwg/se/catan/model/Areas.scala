package de.htwg.se.catan.model

import de.htwg.se.catan.model.impl.fileio.JsonFileIO.JsonLookupResult
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq, XMLOption }
import de.htwg.se.catan.model.impl.fileio.{ JsonDeserializer, JsonSerializable, XMLDeserializer, XMLSerializable }
import play.api.libs.json.*

import scala.xml.Node

/**
 * @author Vincent76;
 */

object Port extends XMLDeserializer[Port] with JsonDeserializer[Port]:
  given portWrites:Writes[Port] = ( o:Port ) => o.toJson
  given portReads:Reads[Port] = ( json:JsValue ) => JsSuccess( fromJson( json ) )

  def fromXML( node:Node ):Port = Port(
    specific = node.childOf( "specific" ).asOption( n => Resource.of( n.content ).get )
  )

  def fromJson( json:JsValue ):Port = Port(
    specific = ( json  \ "specific" ).asOption[Resource]
  )


case class Port( specific:Option[Resource] = None ) extends XMLSerializable with JsonSerializable:
  def toXML:Node = <Port>
    <specific>{ specific.toXML( v => <value>{ v.title }</value> ) }</specific>
  </Port>

  def toJson:JsValue = Json.obj(
    "specific" -> Json.toJson( specific )
  )




abstract class AreaImpl[+T <: Area]( name:String ) extends DeserializerComponentImpl[T]( name ):
  override def init():Unit = Area.addImpl( this )

object Area extends ClassComponent[Area, AreaImpl[Area]]:
  given areaWrites:Writes[Area] = ( o:Area ) => o.toJson
  given areaReads:Reads[Area] = ( json:JsValue ) => JsSuccess( fromJson( json ) )

  def init():Unit =
    WaterArea.init()
    DesertArea.init()
    ResourceArea.init()


abstract class Area( val f:FieldType ) extends XMLSerializable with JsonSerializable



object WaterArea extends AreaImpl[WaterArea]( "WaterArea" ):
  def fromXML( node:Node ):WaterArea =
    WaterArea( port = node.childOf( "port" ).asOption( n => Port.fromXML( n ) ) )

  def fromJson( json:JsValue ):WaterArea =
    WaterArea( port = ( json \ "port" ).asOption[Port] )


case class WaterArea( port:Option[Port] = None ) extends Area( Water ):
  def toXML:Node = <WaterArea>
    <port>{ port.toXML( _.toXML ) }</port>
  </WaterArea>.copy( label = WaterArea.name )

  def toJson:JsValue = Json.obj(
    "class" -> WaterArea.name,
    "port" -> Json.toJson( port )
  )



abstract class LandAreaImpl[+T <: LandArea]( name:String ) extends AreaImpl[T]( name ):
  override def init():Unit =
    super.init()
    LandArea.addImpl( this )

object LandArea extends ClassComponent[LandArea, AreaImpl[LandArea]]:
  given landAreaWrites:Writes[LandArea] = ( o:LandArea ) => o.toJson
  given landAReads:Reads[LandArea] = ( json:JsValue ) => JsSuccess( fromJson( json ) )


abstract class LandArea( override val f:FieldType ) extends Area( f )


object DesertArea extends LandAreaImpl[DesertArea]( "Desert" ):
  override def fromXML( node:Node ):DesertArea = DesertArea()

  override def fromJson( json:JsValue ):DesertArea = DesertArea()


case class DesertArea() extends LandArea( Desert ):
  def toXML:Node = <DesertArea />.copy( label = DesertArea.name )

  def toJson:JsValue = Json.obj(
    "class" -> Json.toJson( DesertArea.name )
  )



object ResourceArea extends LandAreaImpl[ResourceArea]( "ResourceArea" ):
  override def fromXML( node:Node ):ResourceArea = ResourceArea(
    resource = Resource.of( ( node \ "@resource" ).content ).get,
    number = DiceValue.of( ( node \ "@number" ).content.toInt ).get
  )

  override def fromJson( json:JsValue ):ResourceArea = ResourceArea(
    resource = ( json \ "resource" ).as[Resource],
    number = ( json \ "number" ).as[DiceValue]
  )


case class ResourceArea( resource:Resource, number:DiceValue ) extends LandArea( resource ):
  def toXML:Node = <ResourceArea resource={ resource.title } number={ number.value.toString } />.copy( label = ResourceArea.name )

  def toJson:JsValue = Json.obj(
    "class" -> ResourceArea.name,
    "resource" -> Json.toJson( resource ),
    "number" -> Json.toJson( number )
  )

