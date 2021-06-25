package de.htwg.se.catan.aview.gui.impl.gamefield

import de.htwg.se.catan.Catan
import de.htwg.se.catan.aview.gui.gamefield.GameFieldPane.Coords
import de.htwg.se.catan.aview.gui.gamefield.{ GameFieldCanvas, GameFieldPane }
import de.htwg.se.catan.aview.gui.impl.gamefield.ClassicGameFieldCanvasImpl._
import de.htwg.se.catan.model.Card.ResourceCards
import de.htwg.se.catan.model.impl.gamefield.ClassicGameFieldImpl
import de.htwg.se.catan.model._
import de.htwg.se.catan.util._
import scalafx.geometry.VPos
import scalafx.scene.image.Image
import scalafx.scene.paint.Color
import scalafx.scene.text.{ Font, TextAlignment }

import scala.util.Random

object ClassicGameFieldCanvasImpl {
  val portLength:Double = 0.3
  val hexBorderWidth:Double = 2
  val numberSize:Double = 25
  val waterTileImage:Image = new Image( "/water.png" )
  val desertTileImage:Image = new Image( "/desert.png" )
  val resourceTileImages:Map[Resource, List[Image]] = Map(
    Wood -> shuffledImageList( Wood, 4 ),
    Clay -> shuffledImageList( Clay, 3 ),
    Sheep -> shuffledImageList( Sheep, 4 ),
    Wheat -> shuffledImageList( Wheat, 4 ),
    Ore -> shuffledImageList( Ore, 3 ),
  )
  val numberImages:Map[DiceValue, Image] = DiceValue.impls.filter( _.frequency > 0 ).map( n => (n, new Image( "/numbers/" + n.value + ".png" )) ).toMap
  val portImages:Map[Option[Resource], List[Image]] = (Resource.impls.map( Some( _ ) ).toList :+ None).map( r =>
    (r, ClassicGameFieldImpl.adjacentOffset.indices.map( i =>
      new Image( "/ports/" + (if( r.isDefined ) r.get.title.toLowerCase else "unspecific") + "/" + i + ".png" )
    ).toList)
  ).toMap

  private def shuffledImageList( r:Resource, variants:Int ):List[Image] = {
    Random.shuffle( (0 until variants).map( i => new Image( "/" + r.title.toLowerCase + "/" + i + ".png" ) ).toList )
  }
}

class ClassicGameFieldCanvasImpl extends GameFieldCanvas[ClassicGameFieldImpl] {

  graphicsContext2D.textAlign = TextAlignment.Center
  graphicsContext2D.textBaseline = VPos.Center

  protected def doUpdate( gameField:ClassicGameFieldImpl, hWidth:Double, hSize:Double ):Coords = {
    graphicsContext2D.clearRect( 0, 0, width.value, height.value )
    val coords = gameField.hexagons.redByKey( Map.empty:Coords, ( coords:Coords, i:Int ) => {
      val row = gameField.hexagons( i )
      val fRow = row.filter( _.isDefined )
      val nulls = row.size - fRow.size
      fRow.redByKey( coords, ( coords:Coords, j:Int ) => {
        val hex = fRow( j )
        val x = GameFieldPane.padding + (hWidth / 2 * (nulls + 1)) + j * hWidth
        val y = GameFieldPane.padding + hSize + i * (6d / 4d * hSize)
        coords.updated( hex.get, (x, y) )
      } )
    } )
    coords.foreach( d => drawWater( gameField, coords, hWidth, hSize, d._1, d._2 ) )
    //coords.foreach( d => drawWater( gameField, coords, hSize, d._1, d._2 ) ) TEST
    coords.red( ResourceCards.of(), ( tileNumbers:ResourceCards, h:Hex, center:(Double, Double) ) => h.area match {
      case a:LandArea => a.f match {
        case r:Resource if tileNumbers.contains( r ) =>
          val imageNr = tileNumbers( r ) % resourceTileImages( r ).size
          drawHex( hWidth, hSize, h, center, resourceTileImages( r )( imageNr ) )
          tileNumbers.updated( r, imageNr + 1 )
        case Desert =>
          drawHex( hWidth, hSize, h, center, desertTileImage )
          tileNumbers
      }
      case _ => tileNumbers
    } )

    //coords.foreach( d => drawHex( hWidth, hSize, d._1, d._2 ) )
    coords
  }

  private def drawHex( hWidth:Double, hSize:Double, h:Hex, center:(Double, Double), tileImg:Image ):Unit = {
    val points = (0 to 5).map( i => hexCorner( hSize, center, i ) )
    h.area match {
      case a:LandArea =>
        graphicsContext2D.drawImage( tileImg, points( 4 )._1, center._2 - hSize, hWidth, hSize * 2 )
        //graphicsContext2D.fill = GUIApp.colorOf( h.area.f )
        //graphicsContext2D.fillPolygon( points )
        //graphicsContext2D.lineWidth = GameFieldCanvas.hexBorderWidth
        //graphicsContext2D.stroke = Color.Black.brighter
        //graphicsContext2D.strokePolygon( points )
        a match {
          case ResourceArea( _, number ) =>
            /*graphicsContext2D.fill = Color.Black
            val fontSize = GameFieldCanvas.numberSize + number.frequency * GameFieldCanvas.numberMult
            graphicsContext2D.fill = if ( number.frequency >= 5 ) Color.Brown else Color.Black
            graphicsContext2D.font = Font.font( Font.default.getFamily, FontWeight.Bold, GameFieldPane.mult( fontSize, hSize ) )
            graphicsContext2D.fillText( number.value.toString, center._1, center._2 )*/
            val size = GameFieldPane.mult( numberSize, hSize )
            graphicsContext2D.drawImage( numberImages( number ), center._1 - (size / 2), center._2 - (size / 2), size, size )
          case _ =>
        }
      case _ =>
    }
    if( Catan.debug ) {
      graphicsContext2D.fill = Color.White
      graphicsContext2D.font = Font.font( Font.default.getFamily, GameFieldPane.mult( 12, hSize ) )
      graphicsContext2D.fillText( h.id.toString, center._1, center._2 - hSize / 2 )
    }
  }

  private def drawWater( gameField:ClassicGameFieldImpl, coords:Coords, hWidth:Double, hSize:Double, h:Hex, center:(Double, Double) ):Unit = h.area match {
    case w:WaterArea =>
      val image = if( w.port.isDefined ) {
        /*graphicsContext2D.font = Font.font( Font.default.getFamily, FontWeight.Bold, GameFieldPane.mult( 14, hSize ) )
        if ( w.port.get.specific.isDefined ) {
          graphicsContext2D.fill = GUIApp.colorOf( w.port.get.specific.get )
          graphicsContext2D.fillText( w.port.get.specific.get.title, center._1, center._2 )
        } else {
          graphicsContext2D.fill = Color.Black
          graphicsContext2D.fillText( "?", center._1, center._2 )
        }
        graphicsContext2D.stroke = Color.SaddleBrown.darker.darker
        graphicsContext2D.lineWidth = GameFieldPane.mult( 4, hSize )
        gameField.adjacentVertices( h ).filter( _.port == w.port ).foreach( v => {
          val m = GUIApp.middleOf( coords( v.h1 ), coords( v.h2 ), coords( v.h3 ) )
          val a = (center._1 - m._1, center._2 - m._2)
          graphicsContext2D.strokeLine( m._1, m._2,
            m._1 + GameFieldCanvas.portLength * a._1,
            m._2 + GameFieldCanvas.portLength * a._2 )
        } )*/
        /*val img = new Image( "/port_clay.png", hWidth, hSize * 2, true, true )
        graphicsContext2D.save()
        graphicsContext2D.translate( center._1, center._2 )
        graphicsContext2D.rotate( 60 )
        graphicsContext2D.drawImage( img, -hWidth / 2, -hSize )
        graphicsContext2D.restore()*/

        /*val iv = new ImageView( new Image( "/port_clay.png" ) )
        iv.setRotate( Math.cos( 60 ) )
        val params = new SnapshotParameters()
        params.setFill( Color.Transparent )

        graphicsContext2D.drawImage( iv.snapshot( params, null ), ( center._1 - hSize ), ( center._2 - hSize ), hWidth, ( hSize * 2 )  )*/

        //graphicsContext2D.drawImage( new Image( "/port_clay.png" ), hexCorner( hSize, center, 4 )._1, ( center._2 - hSize ), hWidth, ( hSize * 2 ) )

        val rotNr = ClassicGameFieldImpl.adjacentOffset.indices.find( i => gameField.adjacentEdge( h, i ) match {
          case Some( e ) if e.port.isDefined && e.port.get == w.port.get => true
          case _ => false
        } ).getOrElse( 0 )
        portImages( w.port.get.specific )( rotNr )
      }
      else waterTileImage
      graphicsContext2D.drawImage( image, hexCorner( hSize, center, 4 )._1, center._2 - hSize, hWidth, hSize * 2 )
    //graphicsContext2D.stroke = GUIApp.colorOf( Water ).darker.darker.darker
    //graphicsContext2D.lineWidth = GameFieldCanvas.hexBorderWidth
    //graphicsContext2D.strokePolygon( ( 0 to 5 ).map( i => hexCorner( hSize, center, i ) ) )
    case _ =>
  }

  private def hexCorner( hSize:Double, center:(Double, Double), i:Int ):(Double, Double) = {
    val rad = Math.PI / 180 * (60 * i - 30)
    (center._1 + hSize * Math.cos( rad ), center._2 + hSize * Math.sin( rad ))
  }
}
