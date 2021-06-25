package de.htwg.se.catan.aview.tui.impl.gamefield

import de.htwg.se.catan.aview.tui.{ GameDisplay, GameFieldDisplay, TUI }
import de.htwg.se.catan.aview.tui.impl.gamefield.ClassicGameFieldDisplayImpl._
import de.htwg.se.catan.model._
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.util._

/**
 * @author Vincent76;
 */

object ClassicGameFieldDisplayImpl {

  sealed abstract class EdgeDir( val symbol:String )

  case object SouthWest extends EdgeDir( "\\" )

  case object SouthEast extends EdgeDir( "/" )

  case object East extends EdgeDir( "|" )

  case object NorthEast extends EdgeDir( "\\" )

  case object NorthWest extends EdgeDir( "/" )

  case object West extends EdgeDir( "|" )

  type Field = Array[Array[String]]
  type PortOffsets = ((Int, Int), (Int, Int))

  val emptyVertex:String = "O"
  val settlement:String = "n" // "\u2302"
  val city:String = "A" // "\u2656"
  val robber:String = "R" // "\u265F"
  val port:String = "=" // "\u2b40"
  val generalPort:String = "?"

  def colorOf( fieldType:FieldType, background:Boolean = true ):String = fieldType match {
    case Water => if( background ) Console.BLUE_B else Console.BLUE
    case Desert => if( background ) Console.BLACK_B else Console.BLACK
    case Wood => if( background ) Console.GREEN_B else Console.GREEN
    case Clay => if( background ) Console.MAGENTA_B else Console.MAGENTA
    case Sheep => if( background ) Console.WHITE_B else Console.WHITE
    case Wheat => if( background ) Console.YELLOW_B else Console.YELLOW
    case Ore => if( background ) Console.CYAN_B else Console.CYAN
  }

}

case class ClassicGameFieldDisplayImpl(
                                        game:Game,
                                        gameField:ClassicGameFieldImpl,
                                        buildableIDs:Option[List[PlacementPoint]]
                                      ) extends GameFieldDisplay {


  def buildGameField:String = {
    val emptyField:Field = Array.fill[Array[String]]( 31 )( Array.fill[String]( 57 )( " " ) )
    val field = gameField.hexagons.redByKey( emptyField, buildRow ).toVector
    val legend = GameDisplay.get( game ).buildGameLegend( game )
    field.view.zipWithIndex.map( d => {
      TUI.reset + d._1.mkString( TUI.reset ) + (if( d._2 - 2 >= 0 && d._2 - 2 < legend.size )
        "\t\t" + legend( d._2 - 2 ).use( l => TUI.reset + l._1 + TUI.reset + "   " + l._2 )
      else "")
    } ).mkString( "\n" ) + "\n"
  }

  private def buildRow( field:Field, i:Int ):Field = {
    val row = gameField.hexagons( i )
    val fRow = row.filter( _.isDefined )
    val nulls = row.size - fRow.size
    fRow.redByKey( field, ( f:Field, j:Int ) => {
      val hex = fRow( j )
      if( hex.isDefined )
        buildHex( f, hex.get, i * 4, nulls * 4 + j * 8 )
      else
        f
    } )
  }

  private def buildHex( f:Field, h:Hex, i:Int, j:Int ):Field = {
    val base = (2 to 4).red( f, ( field:Field, oi:Int ) => (1 to 7).red( field, ( fi:Field, oj:Int ) => {
      fi.fillEmptyArea( h, i + oi, j + oj )
    } ) )
      .fillEmptyArea( h, i + 1, j + 3 )
      .fillEmptyArea( h, i + 1, j + 4 )
      .fillEmptyArea( h, i + 1, j + 5 )
      .fillEmptyArea( h, i + 5, j + 3 )
      .fillEmptyArea( h, i + 5, j + 4 )
      .fillEmptyArea( h, i + 5, j + 5 )
      .update( i + 2, j + 4, ClassicGameFieldDisplayImpl.colorOf( h.area.f ) + (if( h == game.gameField.robberHex )
        (h.area.f match {
          case Desert => TUI.text
          case _ => TUI.textOnColor
        }) + ClassicGameFieldDisplayImpl.robber
      else " ") )
    val f1 = h.area match {
      case d:DesertArea => (if( buildableIDs.isDefined && buildableIDs.get.contains( h ) )
        base.showID( h.id, i + 3, j + 4 )
      else
        base).showOnFields( d.f.title, i + 4, j + 4, TUI.text )
      case a:WaterArea => if( a.port.isDefined )
        if( a.port.get.specific.isDefined )
          base.showOnFields( a.port.get.specific.get.title, i + 3, j + 4, TUI.textOnColor + ClassicGameFieldDisplayImpl.colorOf( a.port.get.specific.get ) )
        else
          base.update( i + 3, j + 4, TUI.text + "?" )
      else base
      case a:ResourceArea =>
        (if( buildableIDs.isDefined && buildableIDs.get.contains( h ) )
          base.showID( h.id, i + 2, j + 4 )
        else base).showOnFields( a.number.value.toString, i + 3, j + 4, ClassicGameFieldDisplayImpl.colorOf( h.area.f ) + TUI.textOnColor )
          .showOnFields( h.area.f.title, i + 4, j + 4, ClassicGameFieldDisplayImpl.colorOf( h.area.f ) + TUI.textOnColor )
    }
    val f2 = f1.addEdge( h, game.gameField.adjacentHex( h, 5 ), i + 3, j, West )
      .addEdge( h, game.gameField.adjacentHex( h, 4 ), i + 1, j + 2, NorthWest )
      .addEdge( h, game.gameField.adjacentHex( h, 3 ), i + 1, j + 6, NorthEast )
      .addVertex( h, game.gameField.adjacentHex( h, 5 ), game.gameField.adjacentHex( h, 4 ), i + 2, j )
      .addVertex( h, game.gameField.adjacentHex( h, 4 ), game.gameField.adjacentHex( h, 3 ), i, j + 4 )
    val f3 = if( game.gameField.adjacentHex( h, 0 ).isEmpty ) {
      f2.addEdge( h, None, i + 5, j + 2, SouthWest )
        .addVertex( h, None, None, i + 4, j )
    } else f2
    val f4 = if( game.gameField.adjacentHex( h, 1 ).isEmpty ) {
      f3.addEdge( h, None, i + 5, j + 6, SouthEast )
        .addVertex( h, None, None, i + 6, j + 4 )
        .addVertex( h, None, None, i + 4, j + 8 )
    } else f3
    if( game.gameField.adjacentHex( h, 2 ).isEmpty ) {
      f4.addEdge( h, None, i + 3, j + 8, East )
        .addVertex( h, None, None, i + 2, j + 8 )
    } else f4
  }


  private def isShowingID( buildableIDs:Option[List[PlacementPoint]], e:Edge ):Boolean = buildableIDs.isDefined && buildableIDs.get.contains( e )

  private def isShowingID( buildableIDs:Option[List[PlacementPoint]], v:Vertex ):Boolean = buildableIDs.isDefined && buildableIDs.get.contains( v )

  private implicit class RichField( val f:Field ) {
    def update( i:Int, j:Int, s:String ):Field = f.updated( i, f( i ).updated( j, s ) )

    def fillEmptyArea( h:Hex, i:Int, j:Int ):Field = f.update( i, j, colorOf( h.area.f ) + " " )

    def showOnFields( string:String, i:Int, j:Int, c:String = "" ):Field = {
      val start = j - (math.ceil( string.length / 2d ) - 1).toInt
      string.toList.view.zipWithIndex.red( f, ( fi:Field, s:(Char, Int) ) => fi.update( i, start + s._2, c + s._1.toString ) )
    }

    def showID( id:Int, i:Int, j:Int, c:String = "" ):Field = showOnFields( "_" + id.toString, i, j, c )

    def addEdge( h1:Hex, hex2:Option[Hex], i:Int, j:Int, dir:EdgeDir ):Field = {
      if( hex2.isEmpty )
        return f.update( i, j, dir.symbol )
      val edge = gameField.findEdge( h1, hex2.get )
      if( edge.isEmpty )
        return f.update( i, j, dir.symbol )
      if( h1.isWater && hex2.get.isWater )
        return f.update( i, j, colorOf( Water ) + " " )
      val f1 = if( edge.get.road.isEmpty )
        if( isShowingID( buildableIDs, edge.get ) )
          f.showID( edge.get.id, i, j )
        else
          f.update( i, j, dir.symbol )
      else
        f.update( i, j, TUI.colorOf( game.players( edge.get.road.get.owner ).color ) + dir.symbol )
      if( edge.get.port.isEmpty )
        return f1
      val c = if( hex2.get.area.isInstanceOf[WaterArea] ) 1 else -1
      val (o1, o2) = if( buildableIDs.isEmpty )
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
      f1.update( i + o1._1 * c, j + o1._2 * c, colorOf( Water ) + TUI.textOnColor + ClassicGameFieldDisplayImpl.port )
        .update( i + o2._1 * c, j + o2._2 * c, colorOf( Water ) + TUI.textOnColor + ClassicGameFieldDisplayImpl.port )
    }

    def addVertex( h1:Hex, hex2:Option[Hex], hex3:Option[Hex], i:Int, j:Int ):Field = {
      if( hex2.isEmpty || hex3.isEmpty )
        if( i == 0 || i == f.length - 1 )
          return f.update( i, j, " " )
        else
          return f.update( i, j, "|" )
      val vertex = game.gameField.findVertex( h1, hex2.get, hex3.get )
      if( vertex.isEmpty )
        return f.update( i, j, " " )
      if( vertex.get.building.isEmpty )
        if( isShowingID( buildableIDs, vertex.get ) )
          return f.showID( vertex.get.id, i, j )
        else
          return f.update( i, j, ClassicGameFieldDisplayImpl.emptyVertex )
      val player = game.players( vertex.get.building.get.owner )
      if( isShowingID( buildableIDs, vertex.get ) )
        return f.showID( vertex.get.id, i, j, TUI.colorOf( player.color ) )
      vertex.get.building.get match {
        case _:Settlement => f.update( i, j, TUI.colorOf( player.color ) + ClassicGameFieldDisplayImpl.settlement )
        case _:City => f.update( i, j, TUI.colorOf( player.color ) + ClassicGameFieldDisplayImpl.city )
        case _ => f.update( i, j, TUI.colorOf( player.color ) + "?" )
      }
    }
  }

  private implicit class RichPortOffsets( val o:PortOffsets ) {
    def checkIDs( ids:Boolean ):PortOffsets = {
      if( !ids ) return o
      ((c( o._1._1 ), c( o._1._2 )), (c( o._2._1 ), c( o._2._2 )))
    }

    private def c( i:Int ):Int = if( i.abs == 2 ) i / 2 else i
  }

}
