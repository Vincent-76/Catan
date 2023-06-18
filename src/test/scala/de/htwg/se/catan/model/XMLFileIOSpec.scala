package de.htwg.se.catan.model

import com.google.inject.{ Guice, Injector }
import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.model.commands.InitGameCommand
import de.htwg.se.catan.model.impl.fileio.XMLFileIO.*
import de.htwg.se.catan.model.impl.fileio.{ XMLFileIO, XMLParseError }
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.File
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.xml.{ Node, Utility }

class XMLFileIOSpec extends AnyWordSpec with Matchers {
  implicit class XMLNodeTest( node:Node ) {
    def trim:String = Utility.trim( node ).toString
  }

  CatanModule.init()
  val injector:Injector = Guice.createInjector( new CatanModule( test = true ) )
  "XMLParseError" when {
    val parseError = XMLParseError( expected = "expected", got = "got" )
    "new" should {
      "toString" in {
        parseError.toString shouldBe "XMLParseError: Expected -> 'expected', Got -> 'got'"
      }
    }
  }
  "XMLFileIO" when {
    "created" should {
      "save and load" in {
        val game = injector.getInstance( classOf[ClassicGameImpl] )
        val undoStack = List( InitGameCommand() )
        val redoStack = List( InitGameCommand() )
        val path = Await.result( XMLFileIO.save( game, undoStack, redoStack ), Duration.Inf )
        val (game2, undoStack2, redoStack2) = XMLFileIO.load( path )
        val file = new File( path )
        if( file.exists )
          file.delete()
        game2.asInstanceOf[impl.game.ClassicGameImpl].copy( playerFactory = null ) shouldBe
          game.copy( playerFactory = null )
        undoStack2 shouldBe undoStack
        redoStack2 shouldBe redoStack
      }
    }
  }
  "XMLOption" should {
    val o = Some( "Test" )
    val o2 = Option.empty[String]
    "toXML" in {
      o.toXML( v => v ).trim shouldBe <Some><v>{ "Test" }</v></Some>.trim
      o2.toXML( v => v ).trim shouldBe <None />.trim
    }
  }
  "XMLSequence" should {
    val s = Seq( "one", "two", "three" )
    "toXML" in {
      s.toXML( v => v ).trim shouldBe <List>
        <entry><v>one</v></entry>
        <entry><v>two</v></entry>
        <entry><v>three</v></entry>
      </List>.trim
    }
  }
  "XMLMap" should {
    val m = Map( 1 -> "one", 2 -> "two", 3 -> "three" )
    "toXML" in {
      m.toXML( k => k, v => v ).trim shouldBe <Map>
        <entry>
          <key><v>1</v></key>
          <value><v>one</v></value>
        </entry>
        <entry>
          <key><v>2</v></key>
          <value><v>two</v></value>
        </entry>
        <entry>
          <key><v>3</v></key>
          <value><v>three</v></value>
        </entry>
      </Map>.trim
    }
  }
  "XMLTuple2" should {
    val t = ("one", 1)
    "toXML" in {
      t.toXML( v => v, v => v ).trim shouldBe <Tuple2>
        <value1><v>one</v></value1>
        <value2><v>1</v></value2>
      </Tuple2>.trim
    }
  }
  "XMLTuple3" should {
    val t = ("one", 1, 2.5)
    "toXML" in {
      t.toXML( v => v, v => v, v => v ).trim shouldBe <Tuple3>
        <value1><v>one</v></value1>
        <value2><v>1</v></value2>
        <value3><v>2.5</v></value3>
      </Tuple3>.trim
    }
  }
  "XMLNodeSeq" should {
    val xml = <Test data="test  " />
    "content" in {
      ( xml \ "@data" ).content shouldBe "test"
    }
  }
  "XMLNode" should {
    "firstChild" in {
      val child = <Child />
      val xml = <Wrap>{ child }</Wrap>
      xml.firstChild() shouldBe Some( child )
    }
    "childOf" in {
      val child = <Child />
      val xml = <Wrap><Data>{ child }</Data></Wrap>
      xml.childOf( "Data" ) shouldBe child
      intercept[XMLParseError] {
        xml.childOf( "Test" )
      }
      intercept[XMLParseError] {
        <Wrap><Data></Data></Wrap>.childOf( "Data" )
      }
    }
    "asSeq" in {
      val s = Seq( "one", "two", "three" )
      s.toXML( v => v ).asSeq( _.content ) shouldBe s
      intercept[XMLParseError] {
        <Test />.asSeq( _.content )
      }
    }
    "asList" in {
      val l = List( "one", "two", "three" )
      l.toXML( v => v ).asList( _.content ) shouldBe l
    }
    "asVector" in {
      val v = Vector( "one", "two", "three" )
      v.toXML( v => v ).asVector( _.content ) shouldBe v
    }
    "asOption" in {
      <Some><v>Test</v></Some>.asOption( _.content ) shouldBe Some( "Test" )
      <None/>.asOption( _.content ) shouldBe None
      intercept [XMLParseError] {
        <Test />.asOption( _.content )
      }
    }
    "asMap" in {
      val m = Map( 1 -> "one", 2 -> "two", 3 -> "three" )
      m.toXML( k => k, v => v ).asMap( _.content.toInt, _.content ) shouldBe m
      intercept[XMLParseError] {
        <Test />.asMap( _.content.toInt, _.content )
      }
    }
    "asTuple" in {
      val t2 = ("one", 1)
      t2.toXML( v => v, v => v ).asTuple( _.content, _.content.toInt ) shouldBe t2
      intercept[XMLParseError] {
        <Test/>.asTuple( _.content, _.content )
      }
      val t3 = ("one", 1, 2.5)
      t3.toXML( v => v, v => v, v => v ).asTuple( _.content, _.content.toInt, _.content.toDouble ) shouldBe t3
      intercept[XMLParseError] {
        <Test/>.asTuple( _.content, _.content, _.content )
      }
    }
  }
}
