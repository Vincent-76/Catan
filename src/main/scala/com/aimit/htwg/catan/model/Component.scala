package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.model.impl.fileio.{ JsonDeserializer, JsonParseError, XMLDeserializer, XMLParseError }
import com.aimit.htwg.catan.util.RichString
import play.api.libs.json.JsValue

import scala.xml.Node


abstract class ComponentImpl {
  def init():Unit
}

abstract class NamedComponentImpl( val name:String ) extends ComponentImpl

abstract class DeserializerComponentImpl[+T]( name:String ) extends NamedComponentImpl( name ) with XMLDeserializer[T] with JsonDeserializer[T]



abstract class Component[I] {
  private var implementations:Set[I] = Set.empty

  def addImpl[T <: I]( impl:T ):Unit =
    implementations = implementations + impl

  def impls:Set[I] = implementations
}

abstract class ObjectComponent[I] extends Component[I]

abstract class NamedComponent[I <: NamedComponentImpl] extends Component[I] {
  def hasImpl( name:String ):Boolean = impls.exists( _.name ^= name )

  def findImpl( name:String ):Option[I] = impls.find( _.name ^= name ) match {
    case Some( v ) => Some( v )
    case _ => None
  }
}

abstract class ClassComponent[T, I <: DeserializerComponentImpl[T]] extends NamedComponent[I] with XMLDeserializer[T] with JsonDeserializer[T] {

  def fromXML( node:Node ):T = findImpl( node.label ) match {
    case Some( impl ) => impl.fromXML( node )
    case None => throw XMLParseError( expected = getClass.getSimpleName, got = node.label )
  }

  def fromJson( json:JsValue ):T = {
    val name = ( json \ "class" ).validate[String]
    if( name.isError )
      throw JsonParseError( expected = getClass.getSimpleName, got = name.toString )
    findImpl( name.get ) match {
      case Some( impl ) => impl.fromJson( json )
      case None => throw JsonParseError( expected = getClass.getSimpleName, got = name.get )
    }
  }
}