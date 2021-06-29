package de.htwg.se.catan.model

import de.htwg.se.catan.model.impl.fileio.{ JsonDeserializer, JsonParseError, XMLDeserializer, XMLParseError }
import de.htwg.se.catan.util.RichString
import play.api.libs.json.JsValue

import scala.xml.Node

sealed abstract class Component[I] {
  private var implementations:Set[I] = Set.empty

  def addImpl[T <: I]( impl:T ):Unit =
    implementations = implementations + impl

  def impls:Set[I] = implementations
}


abstract class ObjectComponent[I] extends Component[I]


abstract class ComponentImpl {
  def init()
}


abstract class DeserializerComponentImpl[+T]( var name:String ) extends ComponentImpl with XMLDeserializer[T] with JsonDeserializer[T]

abstract class ClassComponent[T, I <: DeserializerComponentImpl[T]] extends Component[I] with XMLDeserializer[T] with JsonDeserializer[T] {

  def findImpl( name:String ):Either[I, String] = impls.find( _.name ^= name ) match {
    case Some( v ) => Left( v )
    case _ => Right( name )
  }

  def fromXML( node:Node ):T = findImpl( node.label ) match {
    case Left( impl ) => impl.fromXML( node )
    case Right( value ) => throw XMLParseError( expected = getClass.getSimpleName, got = value )
  }

  def fromJson( json:JsValue ):T = {
    val name = ( json \ "class" ).validate[String]
    if( name.isError )
      throw JsonParseError( expected = getClass.getSimpleName, got = name.toString )
    findImpl( name.get ) match {
      case Left( impl ) => impl.fromJson( json )
      case Right( value ) => throw JsonParseError( expected = getClass.getSimpleName, got = value )
    }
  }
}