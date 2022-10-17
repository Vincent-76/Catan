package com.aimit.htwg.catan.view.tui.impl.game

import com.aimit.htwg.catan.view.tui.GameDisplay
import com.aimit.htwg.catan.view.tui.impl.gamefield.ClassicGameFieldDisplayImpl.{ city, colorOf, emptyVertex, generalPort, port, robber, settlement }
import com.aimit.htwg.catan.model.{ Clay, Desert, Ore, Resource, Sheep, Water, Wheat, Wood }
import com.aimit.htwg.catan.model.impl.game.ClassicGameImpl
import com.aimit.htwg.catan.util._

object ClassicGameDisplayImpl extends GameDisplay[ClassicGameImpl] {

  val legend:Vector[(String, String)] = Vector(
    (colorOf( Water ) + " ", "Water"),
    (colorOf( Desert ) + " ", "Desert"),
    (colorOf( Wood ) + " ", "Forest/Wood"),
    (colorOf( Clay ) + " ", "Hills/Clay"),
    (colorOf( Sheep ) + " ", "Pasture/Sheep"),
    (colorOf( Wheat ) + " ", "Field/Wheat"),
    (colorOf( Ore ) + " ", "Mountains/Ore"),
    (emptyVertex, "Empty Vertex"),
    (settlement, "Settlement"),
    (city, "City"),
    (robber, "Robber"),
    (port, "Port, 2:1 if a resource is specified"),
    (generalPort, "3:1 exchange port"),
  )

  protected def doBuildGameLegend( game:ClassicGameImpl ):Vector[(String, String)] = {
    val legend = game.resourceStack.red( this.legend :+ ("", ""), ( l:Vector[(String, String)], r:Resource, amount:Int ) => {
      l :+ (r.title + " Stack", amount.toString)
    } ) :+ ("Dev Stack", game.developmentCards.size.toString)
    val titleLength = legend.map( _._1.length ).max
    legend.map( d => (d._1.toLength( titleLength ), d._2) )
  }

}
