package com.aimit.htwg.catan.model

import com.aimit.htwg.catan.CatanModule
import com.aimit.htwg.catan.model.impl.fileio.{ JsonParseError, XMLParseError }
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{ JsValue, Json }

import scala.xml.Node

class ComponentSpec extends AnyWordSpec with Matchers {
  CatanModule.init()

  object TestDeserializerComponentImpl extends DeserializerComponentImpl[String]( "TestImpl" ) {
    override def fromJson( json:JsValue ):String = "Test"
    override def fromXML( node:Node ):String = "Test"
    override def init( ):Unit = TestClassComponent.addImpl( this )
  }

  object TestClassComponent extends ClassComponent[String, DeserializerComponentImpl[String]]

  TestDeserializerComponentImpl.init()
  "ClassComponent" when {
    "created" should {
      "find implementation" in {
        TestClassComponent.of( "TestImpl" ) shouldBe Some( TestDeserializerComponentImpl )
      }
      "not find implementation" in {
        TestClassComponent.of( "TestImpl2" ) shouldBe None
      }
      "fromXML" in {
        TestClassComponent.fromXML( <TestImpl /> ) shouldBe "Test"
      }
      "not fromXML because of no implementation found" in {
        intercept[XMLParseError] {
          TestClassComponent.fromXML( <Test2/> )
        }
      }
      "fromJson" in {
        TestClassComponent.fromJson( Json.obj( "class" -> "TestImpl" ) ) shouldBe "Test"
      }
      "fail fromJson because of invalid class" in {
        intercept[JsonParseError] {
          TestClassComponent.fromJson( Json.obj( "test" -> "TestImpl2" ) )
        }
      }
      "fail fromJson because of no implementation found" in {
        intercept[JsonParseError] {
          TestClassComponent.fromJson( Json.obj( "class" -> "TestImpl2" ) )
        }
      }
    }
  }
}
