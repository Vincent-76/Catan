package de.htwg.se.settlers.util

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.{Clay, Ore, Resources, Sheep, Wheat, Wood}
import org.scalatest.{Matchers, WordSpec}

import scala.util.{Failure, Random, Success, Try}

/**
 * @author Vincent76;
 */
class UtilSpec extends WordSpec with Matchers {
  "util" when {
    "RichAny" should {
      "check" in {
        true.check( v => v ) shouldBe true
      }
      "validate" in {
        1.validate( _ == 2, 0 ) shouldBe 0
      }
      "use" in {
        Some( "check" ).use( s => if ( s.isDefined ) 1 else 2 ) shouldBe 1
      }
    }
    "RichInt" should {
      "toLength" in {
        25.toLength( 5 ) shouldBe "   25"
        213.toLength( 2 ) shouldBe "213"
      }
    }
    "RichString" should {
      "tab" in {
        "Title:\nPoint1\nPoint2".tab shouldBe "Title:\n\tPoint1\n\tPoint2"
        "Title:\nPoint1\nPoint2".tab( 3 ) shouldBe "Title:\n\t\t\tPoint1\n\t\t\tPoint2"
      }
      "toLength" in {
        "Test".toLength( 6 ) shouldBe "Test  "
        "Test".toLength( 3 ) shouldBe "Tes"
      }
      "space" in {
        "Test".space shouldBe "Test "
        "Test".space( 3 ) shouldBe "Test   "
      }
      "removeSpaces" in {
        " Test  ".removeSpaces() shouldBe "Test"
      }
      "=^" in {
        ( "test" =^ "TeSt" ) shouldBe true
        ( "Test" =^ "tEEst" ) shouldBe false
      }
    }
    "RichRandom" should {
      "element" in {
        Random.element( List( 1, 2, 3 ) ).use( e => e.isDefined && List( 1, 2, 3 ).contains( e.get ) ) shouldBe true
      }
    }
    "RichIterable" should {
      "withType" in {
        ( List.empty :+ "Test1" :+ "Test2" :+ 5 :+ 2 ).withType[String] shouldBe List( "Test1", "Test2" )
      }
      val l = Iterable( 2, 6, 10 )
      "red" in {
        l.red( 2, ( c:Int, v:Int ) => c + v / 2 ) shouldBe 11
      }
      "redByKey" in {
        l.redByKey( 2, ( c:Int, i:Int ) => c + i ) shouldBe 5
      }
      "removeAt" in {
        l.removeAt( 1 ) shouldBe Iterable( 2, 10 )
      }
    }
    "RichGeneralIterable" should {
      "deepFind" in {
        val l = List( List( "three", "five" ), List( "ten", "twelve" ), List( "nineteen", "twenty" ) )
        l.deepFind( ( v:String ) => v == "twelve" ).isDefined shouldBe true
      }
    }
    "RichStringIterable" should {
      "sumLength" in {
        List( "five", "seven", "nine" ).sumLength shouldBe 13
      }
    }
    "RichSequence" should {
      val s = Seq( 3, 5, 8, 10, 23 )
      "red" in {
        s.red( "Numbers:", ( s:String, v:Int ) => s + " " + v ) shouldBe "Numbers: 3 5 8 10 23"
      }
      "redByKey" in {
        s.redByKey( "Keys:", ( s:String, i:Int ) => s + " " + i ) shouldBe "Keys: 0 1 2 3 4"
      }
      "removed" in {
        s.removed( 10 ) shouldBe Seq( 3, 5, 8, 23 )
        s.removed( 1 ) shouldBe s
      }
      "sortBySeq" in {
        val resources:ResourceCards = Map( Ore -> 5, Clay -> 2, Wheat -> 4, Sheep -> 3, Wood -> 1 )
        resources.sortBySeq( Resources.get ) shouldBe Seq( (Wood, 1), (Clay, 2), (Sheep, 3), (Wheat, 4), (Ore, 5) )
      }
    }
    "RichMap" should {
      "red" in {
        Map( 1 -> 5, 3 -> 2, 2 -> 7 ).red( 2, ( s:Int, i:Int, v:Int ) => s + i * v ) shouldBe 27
      }
    }
    "RichMatrix" should {
      "matrixFind" in {
        List( List( 3, 5 ), List( 10, 12 ), List( 19, 22 ) ).matrixFind( _ == 10 ) shouldNot be( None )
        List( List( 3, 5 ), List( 10, 12 ), List( 19, 22 ) ).matrixFind( _ == 11 ) shouldBe None
      }
    }
    "RichOption" should {
      "useOrElse" in {
        Some( 5 ).useOrElse( v => v * 10, 3 ) shouldBe 50
        Option.empty[Int].useOrElse( v => v * 10, 3 ) shouldBe 3
      }
    }
    "RichTry" should {
      val t = new Throwable
      val tr:Try[Int] = Failure( t )
      "throwable" in {
        tr.throwable shouldBe t
        intercept[NullPointerException] {
          Success( 5 ).throwable
        }
      }
      "rethrow" in {
        tr.rethrow[String].isInstanceOf[Try[String]] shouldBe true
        intercept[NullPointerException] {
          Success( 5 ).rethrow
        }
      }
      "failureOption" in {
        tr.failureOption shouldNot be( None )
        Success( 5 ).failureOption shouldBe None
      }
    }
    "RichResourceCards" should {
      val cards = ResourceCards.of( 4, 2, 3, 7, 1 )
      "add" in {
        cards.add( Clay, 2 ) shouldBe ResourceCards.of( 4, 4, 3, 7, 1 )
        cards.add( ResourceCards.of( 4, 2 ) ) shouldBe
          ResourceCards.of( 8, 4, 3, 7, 1 )
      }
      "subtract" in {
        cards.subtract( Wheat, 7 ) shouldBe
          Success( ResourceCards.of( 4, 2, 3, 0, 1 ) )
        cards.subtract( Clay, 3 ).isFailure shouldBe true
        cards.subtract( ResourceCards.of( 2 ) ) shouldBe
          Success( ResourceCards.of( 2, 2, 3, 7, 1 ) )
        cards.subtract( ResourceCards.of( 5 ) ).isFailure shouldBe true
      }
      "amount" in {
        cards.amount shouldBe 17
      }
      "has" in {
        cards.has( ResourceCards.of( 2, 1, 3, 5, 1 ) ) shouldBe true
      }
      "sort" in {
        cards.sort shouldBe Seq( (Wood, 4), (Clay, 2), (Sheep, 3), (Wheat, 7), (Ore, 1) )
      }
      "toString" in {
        cards.toString( "+" ) shouldBe "+2 Clay, +3 Sheep, +7 Wheat, +1 Ore, +4 Wood"
      }
    }
  }
}
