package de.htwg.se.catan.model

import com.google.inject.{ Guice, Injector }
import de.htwg.se.catan.CatanModule
import de.htwg.se.catan.model.impl.fileio.{ JsonFileIO, JsonParseError, JsonSerializable }
import de.htwg.se.catan.model.impl.fileio.JsonFileIO._
import de.htwg.se.catan.model.impl.game.ClassicGameImpl
import org.scalatest.{ Matchers, WordSpec }
import play.api.libs.json.{ JsDefined, JsUndefined, Json }

class JsonFileIOSpec extends WordSpec with Matchers {
  CatanModule.init()
  val injector:Injector = Guice.createInjector( new CatanModule( test = true ) )
  "JsonParseError" when {
    val parseError = JsonParseError( expected = "expected", got = "got" )
    "new" should {
      "toString" in {
        parseError.toString shouldBe "JsonParseError: Expected -> 'expected', Got -> 'got'"
      }
    }
  }
  "JsonFileIO" when {
    "created" should {
      "save and load" in {
        val game = injector.getInstance( classOf[ClassicGameImpl] )
        val path = JsonFileIO.save( game )
        val game2 = JsonFileIO.load( path );
        game2.asInstanceOf[impl.game.ClassicGameImpl].copy( playerFactory = null ) shouldBe
          game.copy( playerFactory = null )
      }
    }
    "writeJsonSerializable" in {
      val turn = injector.getInstance( classOf[Turn] ).asInstanceOf[JsonSerializable]
      turn.toJson shouldBe Json.toJson( turn )
    }
  }
  "JsonSeq" should {
    val l = List( "one", "two", "three" )
    val jsonString = "[\"one\",\"two\",\"three\"]"
    "toJson" in {
      l.toJson.toString shouldBe jsonString
    }
    "toJsonC" in {
      l.toJsonC( Json.toJson ).toString shouldBe jsonString
    }
  }
  "JsonMap" should {
    val m = Map( 1 -> "one", 2 -> "two", 3 -> "three" )
    val jsonString = "[[1,\"one\"],[2,\"two\"],[3,\"three\"]]"
    "toJson" in {
      m.toJson.toString shouldBe jsonString
    }
    "toJsonC" in {
      m.toJsonC( Json.toJson, Json.toJson ).toString shouldBe jsonString
    }
  }
  "JsonTuple2" should {
    val t = ("one", "two")
    val jsonString = "[\"one\",\"two\"]"
    "toJson" in {
      t.toJson.toString shouldBe jsonString
    }
    "toJsonC" in {
      t.toJsonC( Json.toJson, Json.toJson ).toString shouldBe jsonString
    }
  }
  "JsonTuple3" should {
    val t = ("one", "two", "three")
    val jsonString = "[\"one\",\"two\",\"three\"]"
    "toJson" in {
      t.toJson.toString shouldBe jsonString
    }
    "toJsonC" in {
      t.toJsonC( Json.toJson, Json.toJson, Json.toJson ).toString shouldBe jsonString
    }
  }
  "JsonValue" should {
    "asOption" in {
      val o = Some( "Test" )
      Json.toJson( o ).asOption[String] shouldBe o
      val o2 = Option.empty[String]
      Json.toJson( o2 ).asOption[String] shouldBe o2
    }
  }
  "JsonLookupResult" should {
    "asOption" in {
      val o = Some( "Test" )
      JsDefined( Json.toJson( o ) ).asOption[String] shouldBe o
      JsUndefined( "" ).asOption[String] shouldBe None
    }
    "asSeq" in {
      val s = Seq( "one", "two", "three" )
      JsDefined( Json.toJson( s ) ).asSeq[String] shouldBe s
      JsDefined( Json.toJson( s ) ).asSeqC( _.as[String] ) shouldBe s
      intercept[JsonParseError] {
        JsDefined( Json.toJson( "Test" ) ).asSeq[String]
      }
    }
    "asList" in {
      val l = List( "one", "two", "three" )
      JsDefined( Json.toJson( l ) ).asList[String] shouldBe l
      JsDefined( Json.toJson( l ) ).asListC( _.as[String] ) shouldBe l
    }
    "asVector" in {
      val v = Vector( "one", "two", "three" )
      JsDefined( Json.toJson( v ) ).asVector[String] shouldBe v
      JsDefined( Json.toJson( v ) ).asVectorC( _.as[String] ) shouldBe v
    }
    "asMap" in {
      val m = Map( 1 -> "one", 2 -> "two", 3 -> "three" )
      val res = Json.toJson( m )
      JsDefined( res ).asMap[Int, String] shouldBe m
      JsDefined( Json.toJson( m ) ).asMapC( _.as[Int], _.as[String] ) shouldBe m
      intercept[JsonParseError] {
        JsDefined( Json.toJson( "Test" ) ).asMap[Int, String]
      }
      intercept[JsonParseError] {
        JsDefined( Json.toJson( List( "one", "two", "three" ) ) ).asMap[Int, String]
      }
    }
    "asTuple" in {
      val t2 = ("one", 1)
      JsDefined( t2.toJson ).asTuple[String, Int] shouldBe t2
      JsDefined( t2.toJson ).asTupleC( _.as[String], _.as[Int] ) shouldBe t2
      intercept[JsonParseError] {
        JsDefined( Json.toJson( "Test" ) ).asTuple[String, Int]
      }
      val t3 = ("one", 1, 2.5)
      JsDefined( t3.toJson ).asTuple[String, Int, Double] shouldBe t3
      JsDefined( t3.toJson ).asTupleC( _.as[String], _.as[Int], _.as[Double] ) shouldBe t3
      intercept[JsonParseError] {
        JsDefined( Json.toJson( "Test" ) ).asTuple[String, Int, Double]
      }
    }
  }
}
