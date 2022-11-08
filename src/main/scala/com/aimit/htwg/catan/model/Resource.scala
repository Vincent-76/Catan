package com.aimit.htwg.catan.model

/**
 * @author Vincent76;
 */

object FieldType extends NamedComponent[FieldType] {
  Water.init()
  Desert.init()
}

abstract class FieldType( name:String ) extends NamedComponentImpl( name ) {
  override def init():Unit = FieldType.addImpl( this )
}

case object Water extends FieldType( "Water" )

case object Desert extends FieldType( "Desert" )


object Resource extends NamedComponent[Resource] {
  Wood.init()
  Clay.init()
  Sheep.init()
  Wheat.init()
  Ore.init()
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