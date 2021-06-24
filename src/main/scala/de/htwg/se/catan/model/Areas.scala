package de.htwg.se.catan.model

import de.htwg.se.catan.model.impl.fileio.XMLFileIO.{ XMLNode, XMLNodeSeq, XMLOption }
import de.htwg.se.catan.model.impl.fileio.XMLSerializable

import scala.xml.Node

/**
 * @author Vincent76;
 */

object Area {
  def fromXML( node:Node ):Area = node.label match {
    case "WaterArea" => WaterArea( port = node.childOf( "port" ).toOption( n => Port.fromXML( n ) ) )
    case "DesertArea" => DesertArea
    case "ResourceArea" =>
      ResourceArea(
      resource = Resources.of( ( node \ "@resource" ).content ).get,
      number = DiceValues.of( ( node \ "@number" ).content.toInt ).get
    )
  }
}

abstract class Area( val f:FieldType ) extends XMLSerializable

case class WaterArea( port:Option[Port] = None ) extends Area( Water ) {
  def toXML:Node = <WaterArea>
    <port>{ port.toXML( _.toXML ) }</port>
  </WaterArea>
}

object Port {
  def fromXML( node:Node ):Port = Port(
    specific = node.childOf( "specific" ).toOption( n => Resources.of( n.content ).get )
  )
}

case class Port( specific:Option[Resource] = None ) extends XMLSerializable {
  def toXML:Node = <Port>
    <specific>{ specific.toXML( v => <value>{ v.title }</value> ) }</specific>
  </Port>
}


abstract class LandArea( override val f:FieldType ) extends Area( f )

case object DesertArea extends LandArea( Desert ) {
  def toXML:Node = <DesertArea />
}

case class ResourceArea( resource:Resource, number:DiceValue ) extends LandArea( resource ) {
  def toXML:Node = <ResourceArea resource={ resource.title } number={ number.value.toString } />
}
