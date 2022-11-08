package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.model.impl.fileio.{ JsonDeserializer, JsonParseError, XMLDeserializer, XMLParseError }
import com.aimit.htwg.catan.util.RichString
import play.api.libs.json.{ JsSuccess, JsValue, Json, Reads, Writes }

import scala.xml.Node


abstract class ComponentImpl {
  def init():Unit
}

abstract class SerialComponentImpl[T]( val id:T ) extends ComponentImpl {
  override def toString:String = id.toString
}

abstract class NamedComponentImpl( val name:String ) extends SerialComponentImpl[String]( name )

abstract class DeserializerComponentImpl[+T]( name:String ) extends NamedComponentImpl( name ) with XMLDeserializer[T] with JsonDeserializer[T]



abstract class Component[I] {
  private var implementations:Set[I] = Set.empty

  def addImpl[T <: I]( impl:T ):Unit =
    implementations = implementations + impl

  def impls:Set[I] = implementations
}

abstract class SerialComponent[T, I <: SerialComponentImpl[T]] extends Component[I] {
  implicit val devCardWrites:Writes[I] = ( impl:I ) => Json.toJson( impl.id.toString )
  implicit val devCardsReads:Reads[I] = ( json:JsValue ) => JsSuccess( impls.find( _.toString == json.as[String] ).get )

  def hasImpl( id:T ):Boolean = impls.exists( _.id == id )

  def of( id:T ):Option[I] = impls.find( _.id == id )
}

abstract class NamedComponent[I <: NamedComponentImpl] extends SerialComponent[String, I] {
  override def hasImpl( name:String ):Boolean = impls.exists( _.name ^= name )

  override def of( name:String ):Option[I] = impls.find( _.name ^= name )
}

abstract class ClassComponent[T, I <: DeserializerComponentImpl[T]] extends NamedComponent[I] with XMLDeserializer[T] with JsonDeserializer[T] {

  def fromXML( node:Node ):T = of( node.label ) match {
    case Some( impl ) => impl.fromXML( node )
    case None => throw XMLParseError( expected = getClass.getSimpleName, got = node.label )
  }

  def fromJson( json:JsValue ):T = {
    val name = ( json \ "class" ).validate[String]
    if( name.isError )
      throw JsonParseError( expected = getClass.getSimpleName, got = name.toString )
    of( name.get ) match {
      case Some( impl ) => impl.fromJson( json )
      case None => throw JsonParseError( expected = getClass.getSimpleName, got = name.get )
    }
  }
}