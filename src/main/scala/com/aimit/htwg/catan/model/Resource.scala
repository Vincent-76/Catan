package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.util.RichString
import play.api.libs.json._

/**
 * @author Vincent76;
 */

object FieldType extends ObjectComponent[FieldType] {
  implicit val fieldTypeWrites:Writes[FieldType] = ( o:FieldType ) => Json.toJson( o.title )
  implicit val fieldTypeReads:Reads[FieldType] = ( json:JsValue ) => JsSuccess( of( json.as[String] ).get )

  Water.init()
  Desert.init()

  def of( s:String ):Option[FieldType] = impls.find( _.title ^= s )
}

abstract class FieldType( val title:String ) extends ComponentImpl {
  override def init():Unit = FieldType.addImpl( this )

  override def toString:String = title
}

case object Water extends FieldType( "Water" )

case object Desert extends FieldType( "Desert" )


object Resource extends ObjectComponent[Resource] {
  implicit val resourceWrites:Writes[Resource] =
    ( o:Resource ) => Json.toJson( o.title )
  implicit val resourceReads:Reads[Resource] =
    ( json:JsValue ) => JsSuccess( of( json.as[String] ).get )

  Wood.init()
  Clay.init()
  Sheep.init()
  Wheat.init()
  Ore.init()

  def of( s:String ):Option[Resource] =
    impls.find( _.title ^= s )
}

abstract class Resource( val index:Int, title:String ) extends FieldType( title ) {
  override def init():Unit = {
    super.init()
    Resource.addImpl( this )
  }
}

case object Wood extends Resource( 0, "Wood" )

case object Clay extends Resource( 1, "Clay" )

case object Sheep extends Resource( 2, "Sheep" )

case object Wheat extends Resource( 3, "Wheat" )

case object Ore extends Resource( 4, "Ore" )