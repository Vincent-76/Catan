package de.htwg.se.catan.model.impl.fileio

import de.htwg.se.catan.model.{ Command, FileIO, Game }

import java.io
import scala.reflect.io.File
import scala.xml.{ Elem, Node, NodeSeq, PrettyPrinter, Utility }

trait XMLSerializable {
  def toXML:Node
}

trait XMLDeserializer[+T] {
  def fromXML( node:Node ):T
}

case class XMLParseError( expected:String, got:String ) extends RuntimeException {
  override def toString:String = "XMLParseError: Expected -> '" + expected + "', Got -> '" + got + "'"
}

object XMLFileIO extends FileIO( "xml" ) {

  override def load( path:String ):(Game, List[Command], List[Command]) = {
    val xml = scala.xml.XML.loadFile( path )
    val game = Game.fromXML( xml.childOf( "game" ) )
    val undoStack = xml.childOf( "undoStack" ).asList( n => Command.fromXML( n ) )
    val redoStack = xml.childOf( "redoStack" ).asList( n => Command.fromXML( n ) )
    (game, undoStack, redoStack)
  }

  override def save( game:Game, undoStack:List[Command], redoStack:List[Command] ):String = {
    val file = File( getFileName )
    val save = <Save>
      <game>{ game.toXML }</game>
      <undoStack>{ undoStack.toXML( _.toXML ) }</undoStack>
      <redoStack>{ redoStack.toXML( _.toXML ) }</redoStack>
    </Save>
    scala.xml.XML.save( file.toAbsolute.path, save )
    file.toAbsolute.path
  }

  private def wrap( data:io.Serializable ):io.Serializable = data match {
    case n:Node => n
    case s => <v>{s}</v>
  }

  implicit class XMLOption[T]( val option:Option[T] ) {
    def toXML( valBuilder:T => io.Serializable ):Node = option match {
      case Some( e ) => <Some>{wrap( valBuilder( e ) )}</Some>
      case _ => <None/>
    }
  }

  implicit class XMLSequence[E]( val list:Seq[E] ) {
    def toXML( valBuilder:E => io.Serializable ):Node = <List>
      {list.map( e =>
        <entry>{wrap( valBuilder( e ) )}</entry>
      )}
    </List>
  }

  implicit class XMLMap[K, V]( val map:Map[K, V] ) {
    def toXML( keyBuilder:K => io.Serializable, valBuilder:V => io.Serializable ):Node = <Map>
      {map.map( d =>
        <entry>
          <key>{wrap( keyBuilder( d._1 ) )}</key>
          <value>{wrap( valBuilder( d._2 ) )}</value>
        </entry>
      ).toSeq}
    </Map>
  }

  implicit class XMLTuple2[T1, T2]( val tuple:(T1, T2) ) {
    def toXML( builder1:T1 => io.Serializable, builder2:T2 => io.Serializable ):Node = <Tuple2>
      <value1>{wrap( builder1( tuple._1 ) )}</value1>
      <value2>{wrap( builder2( tuple._2 ) )}</value2>
    </Tuple2>
  }

  implicit class XMLTuple3[T1, T2, T3]( val tuple:(T1, T2, T3) ) {
    def toXML( builder1:T1 => io.Serializable, builder2:T2 => io.Serializable, builder3:T3 => io.Serializable ):Node = <Tuple3>
      <value1>{wrap( builder1( tuple._1 ) )}</value1>
      <value2>{wrap( builder2( tuple._2 ) )}</value2>
      <value3>{wrap( builder3( tuple._3 ) )}</value3>
    </Tuple3>
  }

  implicit class XMLNodeSeq( nodeSeq:NodeSeq ) {
    def content:String = nodeSeq.text.trim
  }

  implicit class XMLNode( node:Node ) {
    def firstChild( ):Option[Node] = {
      val r = node.child.collectFirst {
        case e:Elem => e
      }
      r
    }

    def childOf( tag:String ):Node = {
      val child = (node \ tag).headOption
      if( child.isEmpty )
        throw XMLParseError( expected = tag, got = "Nothing" )
      val c = child.get.firstChild()
      c match {
        case Some( c ) => c
        case None => throw XMLParseError( expected = "Content in tag[" + tag + "]", got = "Nothing" )
      }
    }

    def asSeq[E]( builder:Node => E ):Seq[E] = node.label match {
      case "List" => (node \ "entry").map( n => builder( n.firstChild().get ) )
      case e => throw XMLParseError( expected = "List", got = e )
    }

    def asList[E]( builder:Node => E ):List[E] = asSeq( builder ).toList

    def asVector[E]( builder:Node => E ):Vector[E] = asSeq( builder ).toVector

    def asOption[E]( builder:Node => E ):Option[E] = node.label match {
      case "Some" => Some( builder( node.firstChild().get ) )
      case "None" => None
      case e => throw XMLParseError( expected = "Option", got = e )
    }

    def asMap[K, V]( keyBuilder:Node => K, valBuilder:Node => V ):Map[K, V] =
      asMapC( ( keyNode, valNode ) => (keyBuilder( keyNode ), valBuilder( valNode )) )

    def asMapC[K, V]( builder:(Node, Node) => (K, V) ):Map[K, V] = node.label match {
      case "Map" => (node \ "entry").map( n => builder( n.childOf( "key" ), n.childOf( "value" ) ) ).toMap
      case e => throw XMLParseError( expected = "Map", got = e )
    }

    def asTuple[T1, T2]( builder1:Node => T1, builder2:Node => T2 ):(T1, T2) = node.label match {
      case "Tuple2" => (
        builder1( node.childOf( "value1" ) ),
        builder2( node.childOf( "value2" ) )
      )
      case e => throw XMLParseError( expected = "Tuple2", got = e )
    }

    def asTuple[T1, T2, T3]( builder1:Node => T1, builder2:Node => T2, builder3:Node => T3 ):(T1, T2, T3) = node.label match {
      case "Tuple3" => (
        builder1( node.childOf( "value1" ) ),
        builder2( node.childOf( "value2" ) ),
        builder3( node.childOf( "value3" ) )
      )
      case e => throw XMLParseError( expected = "Tuple3", got = e )
    }
  }

}
