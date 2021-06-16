package de.htwg.se.catan.model


abstract class Structure( val owner:PlayerID )

abstract class Building( owner:PlayerID ) extends Structure( owner )

case class Road( override val owner:PlayerID ) extends Structure( owner )

case class Settlement( override val owner:PlayerID ) extends Building( owner )

case class City( override val owner:PlayerID ) extends Building( owner )
