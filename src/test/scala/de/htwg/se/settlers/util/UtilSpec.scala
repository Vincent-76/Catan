package de.htwg.se.settlers.util

import de.htwg.se.settlers.model.Cards.ResourceCards
import de.htwg.se.settlers.model.{ Clay, Ore, Sheep, Wheat, Wood }
import org.scalatest.{ Matchers, WordSpec }

import scala.util.{ Failure, Random, Success, Try }

/**
 * @author Vincent76;
 */
class UtilSpec extends WordSpec with Matchers {
  "util" when {
    "RichAny" should {
      "check" in {
        true.check( v => v ) should be( true )
      }
      "validate" in {
        1.validate( _ == 2, 0 ) should be( 0 )
      }
      "use" in {
        Some( "check" ).use( s => if ( s.isDefined ) 1 else 2 ) should be( 1 )
      }
    }
    "RichInt" should {
      "toLength" in {
        25.toLength( 5 ) should be( "   25" )
      }
    }
    "RichString" should {
      "tab" in {
        "Title:\nPoint1\nPoint2".tab() should be( "Title:\n\tPoint1\n\tPoint2" )
        "Title:\nPoint1\nPoint2".tab( 3 ) should be( "Title:\n\t\t\tPoint1\n\t\t\tPoint2" )
      }
      "toLength" in {
        "Test".toLength( 6 ) should be( "Test  " )
      }
      "space" in {
        "Test".space should be( "Test " )
        "Test".space( 3 ) should be( "Test   " )
      }
      "removeSpaces" in {
        " Test  ".removeSpaces() should be( "Test" )
      }
      "=^" in {
        ( "test" =^ "TeSt" ) should be( true )
        ( "Test" =^ "tEEst" ) should be( false )
      }
    }
    "RichRandom" should {
      "element" in {
        Random.element( List( 1, 2, 3 ) ).use( e => e.isDefined && List( 1, 2, 3 ).contains( e.get ) ) should be( true )
      }
    }
    "RichIterable" should {
      "withType" in {
        ( List.empty :+ "Test1" :+ "Test2" :+ 5 :+ 2 ).withType[String] should be( List( "Test1", "Test2" ) )
      }
      val l = Iterable( 2, 6, 10 )
      "red" in {
        l.red( 2, ( c:Int, v:Int ) => c + v / 2 ) should be( 11 )
      }
      "redByKey" in {
        l.redByKey( 2, ( c:Int, i:Int ) => c + i ) should be( 5 )
      }
      "removeAt" in {
        l.removeAt( 1 ) should be( Iterable( 2, 10 ) )
      }
    }
    "RichGeneralIterable" should {
      "deepFind" in {
        val l = List( List( "three", "five" ), List( "ten", "twelve" ), List( "nineteen", "twenty" ) )
        l.deepFind( ( v:String ) => v == "twelve" ).isDefined should be( true )
      }
    }
    "RichStringIterable" should {
      "sumLength" in {
        List( "five", "seven", "nine" ).sumLength should be( 13 )
      }
    }
    "RichSequence" should {
      val s = Seq( 3, 5, 8, 10, 23 )
      "red" in {
        s.red( "Numbers:", ( s:String, v:Int ) => s + " " + v ) should be( "Numbers: 3 5 8 10 23" )
      }
      "redByKey" in {
        s.redByKey( "Keys:", ( s:String, i:Int ) => s + " " + i ) should be( "Keys: 0 1 2 3 4" )
      }
      "removed" in {
        s.removed( 10 ) should be( Seq( 3, 5, 8, 23 ) )
      }
    }
    "RichMap" should {
      "red" in {
        Map( 1 -> 5, 3 -> 2, 2 -> 7 ).red( 2, ( s:Int, i:Int, v:Int ) => s + i * v ) should be( 27 )
      }
    }
    "RichMatrix" should {
      "matrixFind" in {
        List( List( 3, 5 ), List( 10, 12 ), List( 19, 22 ) ).matrixFind( _ == 10 ).isDefined should be( true )
      }
    }
    "RichTry" should {
      val t = new Throwable
      val tr:Try[Int] = Failure( t )
      "throwable" in {
        tr.throwable should be( t )
      }
      "rethrow" in {
        tr.rethrow[String] shouldBe a [Try[String]]
      }
      "failureOption" in {
        tr.failureOption.isDefined should be( true )
      }
    }
    "RichResourceCards" should {
      val cards = ResourceCards.of( 4, 2, 3, 7, 1 )
      "add" in {
        cards.add( Clay, 2 ) should be( ResourceCards.of( 4, 4, 3, 7, 1 ) )
        cards.add( ResourceCards.of( 4, 2 ) ) should be( ResourceCards.of( 8, 4, 3, 7, 1 ) )
      }
      "subtract" in {
        cards.subtract( Wheat, 7 ) should be( Success( ResourceCards.of( 4, 2, 3, 0, 1 ) ) )
        cards.subtract( Clay, 3 ).isFailure should be( true )
        cards.subtract( ResourceCards.of( 2 ) ) should be( Success( ResourceCards.of( 2, 2, 3, 7, 1 ) ) )
        cards.subtract( ResourceCards.of( 5 ) ).isFailure should be( true )
      }
      "amount" in {
        cards.amount should be( 17 )
      }
      "has" in {
        cards.has( ResourceCards.of( 2, 1, 3, 5, 1 ) ) should be( true )
      }
      "sort" in {
        cards.sort should be( Seq( (Wood, 4), (Clay, 2), (Sheep, 3), (Wheat, 7), (Ore, 1) ) )
      }
    }
  }
}
