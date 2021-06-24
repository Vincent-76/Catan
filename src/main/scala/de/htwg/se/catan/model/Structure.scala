package de.htwg.se.catan.model

import de.htwg.se.catan.model.impl.fileio.XMLFileIO.XMLNode
import de.htwg.se.catan.model.impl.fileio.XMLSerializable

import scala.xml.Node

object Structure {
  def fromXML( node:Node ):Structure = node.label match {
    case "Road" => Road( owner = PlayerID.fromXML( node.childOf( "owner" ) ) )
    case "Settlement" => Settlement( owner = PlayerID.fromXML( node.childOf( "owner" ) ) )
    case "City" => City( owner = PlayerID.fromXML( node.childOf( "owner" ) ) )
  }
}

abstract class Structure( val owner:PlayerID ) extends XMLSerializable

abstract class Building( owner:PlayerID ) extends Structure( owner )

case class Road( override val owner:PlayerID ) extends Structure( owner ) {
  def toXML:Node = <Road>
    <owner>{ owner.toXML }</owner>
  </Road>
}

case class Settlement( override val owner:PlayerID ) extends Building( owner ) {
  def toXML:Node = <Settlement>
    <owner>{ owner.toXML }</owner>
  </Settlement>
}

case class City( override val owner:PlayerID ) extends Building( owner ) {
  def toXML:Node = <City>
    <owner>{ owner.toXML }</owner>
  </City>
}
