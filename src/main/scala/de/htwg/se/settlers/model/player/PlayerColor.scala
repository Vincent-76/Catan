package de.htwg.se.settlers.model.player

sealed abstract class PlayerColor( val name:String )

case object Green extends PlayerColor( "Green" )

case object Blue extends PlayerColor( "Blue" )

case object Yellow extends PlayerColor( "Yellow" )

case object Red extends PlayerColor( "Red" )