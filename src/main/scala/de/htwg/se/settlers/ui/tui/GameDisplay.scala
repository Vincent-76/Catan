package de.htwg.se.settlers.ui.tui

import de.htwg.se.settlers.controller.Controller
import de.htwg.se.settlers.model.GameField.{ Edge, Hex, Vertex }
import de.htwg.se.settlers.model._
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
object GameDisplay {
  val emptyVertex:String = "O"
  val settlement:String = "n" // "\u2302"
  val city:String = "A" // "\u2656"
  val robber:String = "R" // "\u265F"
  val port:String = "=" // "\u2b40"
  val generalPort:String = "?"

  val legend:Vector[(String, String)] = Vector(
    (Water.c.b + " ", "Water"),
    (Desert.c.b + " ", "Desert"),
    (Wood.c.b + TUI.textOnColor + " ", "Forest/Wood"),
    (Clay.c.b + " ", "Hills/Clay"),
    (Sheep.c.b + " ", "Pasture/Sheep"),
    (Wheat.c.b + " ", "Field/Wheat"),
    (Ore.c.b + " ", "Mountains/Ore"),
    (Desert.s, "Desert"),
    (Wood.s, "Wood"),
    (Clay.s, "Clay"),
    (Sheep.s, "Sheep"),
    (Wheat.s, "Wheat"),
    (Ore.s, "Ore"),
    (emptyVertex, "Empty Vertex"),
    (settlement, "Settlement"),
    (city, "City"),
    (robber, "Robber"),
    (port, "Port, 2:1 if a resource is specified"),
    (generalPort, "3:1 exchange port"),
  )

  def apply( controller:Controller, placement:Placement, playerID:Int, any:Boolean = false ):GameDisplay = {
    new GameDisplay( controller.game, Some( placement, controller.game.getBuildableIDsForPlayer( playerID, placement, any ) ) )
  }

  def apply( controller:Controller, placement:Placement, buildableIDs:List[Int] ):GameDisplay = {
    new GameDisplay( controller.game, Some( placement, buildableIDs ) )
  }

  def apply( controller:Controller ) = new GameDisplay( controller.game )
}

class GameDisplay( game:Game, placement:Option[(Placement, List[Int])] = Option.empty ) {

  type Field = Array[Array[String]]

  type PortOffsets = ((Int, Int), (Int, Int))

  private def isShowingID( e:Edge ):Boolean = placement.isDefined && placement.get._1 == Road && placement.get._2.contains( e.id )

  private def isShowingID( v:Vertex ):Boolean = placement.isDefined && placement.get._1.isInstanceOf[VertexPlacement] && placement.get._2.contains( v.id )


  def buildGameField:String = {
    val emptyField:Field = Array.fill[Array[String]]( 31 )( Array.fill[String]( 57 )( " " ) )
    val field = game.gameField.hexagons.redByKey( emptyField, buildRow ).toVector
    val legend = getLegend
    field.view.zipWithIndex.map( d => {
      TUI.reset + d._1.mkString( TUI.reset ) + ( if ( d._2 - 2 >= 0 && d._2 - 2 < legend.size )
        "\t\t" + legend( d._2 - 2 ).use( l => TUI.reset + l._1 + TUI.reset + "   " + l._2 )
      else "" ) + "\n"
    } ).mkString
  }


  implicit class RichPortOffsets( val o:PortOffsets ) {
    def checkIDs( ids:Boolean ):PortOffsets = {
      if ( !ids ) return o
      ((c( o._1._1 ), c( o._1._2 )), (c( o._2._1 ), c( o._2._2 )))
    }

    private def c( i:Int ):Int = if ( i.abs == 2 ) i / 2 else i
  }

  implicit class RichField( val f:Field ) {
    def update( i:Int, j:Int, s:String ):Field = f.updated( i, f( i ).updated( j, s ) )

    def fillEmptyArea( h:Hex, i:Int, j:Int ):Field = f.update( i, j, h.area.f.c.b + " " )

    def showOnFields( string:String, i:Int, j:Int, c:String = "" ):Field = {
      val start = j - ( math.ceil( string.length / 2d ) - 1 ).toInt
      string.toList.view.zipWithIndex.red( f, ( fi:Field, s:(Char, Int) ) => fi.update( i, start + s._2, c + s._1.toString ) )
    }

    def showID( id:Int, i:Int, j:Int, c:String = "" ):Field = showOnFields( "_" + id.toString, i, j, c )

    def addEdge( h1:Hex, hex2:Option[Hex], i:Int, j:Int, dir:EdgeDir ):Field = {
      if ( hex2.isEmpty )
        return f.update( i, j, dir.symbol )
      val edge = game.gameField.findEdge( h1, hex2.get )
      if ( edge.isEmpty )
        return f.update( i, j, dir.symbol )
      if ( h1.area.f == Water && hex2.get.area.f == Water )
        return f.update( i, j, Water.c.b + " " )
      val f1 = if ( edge.get.road.isEmpty )
        if ( isShowingID( edge.get ) )
          f.showID( edge.get.id, i, j )
        else
          f.update( i, j, dir.symbol )
      else
        f.update( i, j, game.players( edge.get.road.get.owner ).color.c.t + dir.symbol )
      if ( edge.get.port.isEmpty )
        return f1
      val c = if ( hex2.get.area.isInstanceOf[WaterArea] ) 1 else -1
      val (o1, o2) = if ( placement.isEmpty )
        dir match {
          case SouthWest => ((0, -2), (1, 1))
          case SouthEast => ((0, 2), (1, -1))
          case East => ((-1, 1), (1, 1))
          case NorthEast => ((0, 2), (-1, -1))
          case NorthWest => ((0, -2), (-1, 1))
          case West => ((-1, -1), (1, -1))
        }
      else
        dir match {
          case SouthWest => ((0, -2), (1, 0))
          case SouthEast => ((0, 2), (1, 0))
          case East => ((-1, 2), (1, 2))
          case NorthEast => ((0, 2), (-1, 0))
          case NorthWest => ((0, -2), (-1, 0))
          case West => ((-1, -2), (1, -2))
        }
      f1.update( i + o1._1 * c, j + o1._2 * c, Water.c.b + TUI.textOnColor + GameDisplay.port )
        .update( i + o2._1 * c, j + o2._2 * c, Water.c.b + TUI.textOnColor + GameDisplay.port )
    }

    def addVertex( h1:Hex, hex2:Option[Hex], hex3:Option[Hex], i:Int, j:Int ):Field = {
      if ( hex2.isEmpty || hex3.isEmpty )
        if ( i == 0 || i == f.length - 1 )
          return f.update( i, j, " " )
        else
          return f.update( i, j, "|" )
      val vertex = game.gameField.findVertex( h1, hex2.get, hex3.get )
      if ( vertex.isEmpty )
        return f.update( i, j, " " )
      if ( vertex.get.building.isEmpty )
        if ( isShowingID( vertex.get ) )
          return f.showID( vertex.get.id, i, j )
        else
          return f.update( i, j, GameDisplay.emptyVertex )
      val player = game.players( vertex.get.building.get.owner )
      if ( isShowingID( vertex.get ) )
        return f.showID( vertex.get.id, i, j, player.color.c.t )
      vertex.get.building.get match {
        case _:Settlement => f.update( i, j, player.color.c.t + GameDisplay.settlement )
        case _:City => f.update( i, j, player.color.c.t + GameDisplay.city )
        case _ => f.update( i, j, player.color.c.t + "?" )
      }
    }
  }

  private def getLegend:Vector[(String, String)] = {
    val legend = game.resourceStack.red( GameDisplay.legend :+ ("", ""), ( l:Vector[(String, String)], r:Resource, amount:Int ) => {
      l :+ (r.s + " Stack", amount.toString)
    } ) :+ ("Dev Stack", game.developmentCards.size.toString)
    val titleLength = legend.map( _._1.length ).max
    legend.map( d => (d._1.toLength( titleLength ), d._2) )
  }

  private def buildRow( field:Field, i:Int ):Field = {
    val row = game.gameField.hexagons( i )
    val fRow = row.filter( _.isDefined )
    val nulls = row.size - fRow.size
    fRow.redByKey( field, ( f:Field, j:Int ) => {
      val hex = fRow( j )
      if ( hex.isDefined )
        buildHex( f, hex.get, i * 4, nulls * 4 + j * 8 )
      else
        f
    } )
  }

  private def buildHex( f:Field, h:Hex, i:Int, j:Int ):Field = {
    val base = ( 2 to 4 ).red( f, ( field:Field, oi:Int ) => ( 1 to 7 ).red( field, ( fi:Field, oj:Int ) => {
      fi.fillEmptyArea( h, i + oi, j + oj )
    } ) )
      .fillEmptyArea( h, i + 1, j + 3 )
      .fillEmptyArea( h, i + 1, j + 4 )
      .fillEmptyArea( h, i + 1, j + 5 )
      .fillEmptyArea( h, i + 5, j + 3 )
      .fillEmptyArea( h, i + 5, j + 4 )
      .fillEmptyArea( h, i + 5, j + 5 )
      .update( i + 2, j + 4, h.area.f.c.b + ( if ( h == game.gameField.robber )
        ( h.area.f match {
          case Desert => TUI.text
          case _ => TUI.textOnColor
        } ) + GameDisplay.robber
      else " " ) )
    val f1 = h.area match {
      case DesertArea => ( if ( placement.isDefined && placement.get._1 == Robber && h != game.gameField.robber )
        base.showID( h.id, i + 3, j + 4 )
      else
        base ).showOnFields( DesertArea.f.s, i + 4, j + 4, TUI.text )
      case a:WaterArea => if ( a.port.isDefined )
        if ( a.port.get.specific.isDefined )
          base.showOnFields( a.port.get.specific.get.s, i + 3, j + 4, TUI.textOnColor + a.port.get.specific.get.c.b )
        else
          base.update( i + 3, j + 4, TUI.text + "?" )
      else base
      case a:ResourceArea =>
        ( if ( placement.isDefined && placement.get._1 == Robber && h != game.gameField.robber )
          base.showID( h.id, i + 2, j + 4 )
        else base ).showOnFields( a.number.value.toString, i + 3, j + 4, h.area.f.c.b + TUI.textOnColor )
          .showOnFields( h.area.f.s, i + 4, j + 4, h.area.f.c.b + TUI.textOnColor )
    }
    val f2 = f1.addEdge( h, game.gameField.adjacentHex( h, 5 ), i + 3, j, West )
      .addEdge( h, game.gameField.adjacentHex( h, 4 ), i + 1, j + 2, NorthWest )
      .addEdge( h, game.gameField.adjacentHex( h, 3 ), i + 1, j + 6, NorthEast )
      .addVertex( h, game.gameField.adjacentHex( h, 5 ), game.gameField.adjacentHex( h, 4 ), i + 2, j )
      .addVertex( h, game.gameField.adjacentHex( h, 4 ), game.gameField.adjacentHex( h, 3 ), i, j + 4 )
    val f3 = if ( game.gameField.adjacentHex( h, 0 ).isEmpty ) {
      f2.addEdge( h, Option.empty, i + 5, j + 2, SouthWest )
        .addVertex( h, Option.empty, Option.empty, i + 4, j )
    } else f2
    val f4 = if ( game.gameField.adjacentHex( h, 1 ).isEmpty ) {
      f3.addEdge( h, Option.empty, i + 5, j + 6, SouthEast )
        .addVertex( h, Option.empty, Option.empty, i + 6, j + 4 )
        .addVertex( h, Option.empty, Option.empty, i + 4, j + 8 )
    } else f3
    if ( game.gameField.adjacentHex( h, 2 ).isEmpty ) {
      f4.addEdge( h, Option.empty, i + 3, j + 8, East )
        .addVertex( h, Option.empty, Option.empty, i + 2, j + 8 )
    } else f4
  }


  def buildPlayerDisplay( turnPlayer:Option[Int] = Option.empty ):String = {
    val otherPlayers = if ( turnPlayer.isDefined )
      game.players.filter( _.id != turnPlayer.get )
    else game.players
    val nameLength = otherPlayers.map( _.idName.length ).max
    val s = otherPlayers.map( p => {
      TUI.displayName( p, nameLength ) +
        " Resources[" + p.resources.amount.toLength( 2 ) + "]" +
        " Points[" + p.getDisplayVictoryPoints( game ).toLength( 2 ) + "]" +
        " DevCards[" + p.devCards.size.toLength( 2 ) + "]" +
        " UsedDevCards[" + p.usedDevCards.map( _.t ).mkString( "|" ) + "] " +
        p.getBonusCards( game ).mkString( " " )
    } ).mkString( "\n" )
    ( if ( turnPlayer.isDefined ) {
      val p = game.players( turnPlayer.get )
      val resourceNameLength = Resources.get.map( _.s.length ).max
      s + "\n\n" + TUI.displayName( p ) +
        "\nVictory Points: " + p.getVictoryPoints( game ) +
        "\nResources:\n" +
        p.resources.sort.map( d => "  " + d._1.s.toLength( resourceNameLength ) + "  " + d._2 ).mkString( "\n" ) +
        "\nDevelopment Cards: [" + p.devCards.map( _.t ).mkString( "|" ) + "]" +
        "\nUsed Dev Cards:    [" + p.usedDevCards.map( _.t ).mkString( "|" ) + "]" +
        p.getBonusCards( game ).map( c => "\n" + c.t ).mkString
    } else s ) + "\n\n"
  }
}
