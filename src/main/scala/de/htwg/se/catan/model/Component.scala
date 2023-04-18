package de.htwg.se.catan.model

import de.htwg.se.catan.model.impl.fileio.{ JsonDeserializer, JsonParseError, XMLDeserializer, XMLParseError }
import de.htwg.se.catan.util.^=
import play.api.libs.json.JsValue

import scala.xml.Node

sealed abstract class Component[I]:
  private var implementations:Set[I] = Set.empty

  def addImpl[T <: I]( impl:T ):Unit =
    implementations = implementations + impl

  def impls:Set[I] = implementations


trait ObjectComponent[I] extends Component[I]


abstract class ComponentImpl:
  def init():Unit


trait DeserializerComponentImpl[+T]( val name:String ) extends ComponentImpl with XMLDeserializer[T] with JsonDeserializer[T]


trait ClassComponent[T, I <: DeserializerComponentImpl[T]] extends Component[I] with XMLDeserializer[T] with JsonDeserializer[T]:

  def findImpl( name:String ):I | String = impls.find( _.name ^= name ) match
    case Some( v ) => v
    case _ => name

  def fromXML( node:Node ):T = findImpl( node.label ) match
    case impl:I => impl.fromXML( node )
    case value:String => throw XMLParseError( expected = getClass.getSimpleName, got = value )

  def fromJson( json:JsValue ):T =
    val name = ( json \ "class" ).validate[String]
    if name.isError then
      throw JsonParseError( expected = getClass.getSimpleName, got = name.toString )
    findImpl( name.get ) match
      case impl:I => impl.fromJson( json )
      case value:String => throw JsonParseError( expected = getClass.getSimpleName, got = value )