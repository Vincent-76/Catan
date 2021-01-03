package de.htwg.se.settlers.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.settlers.util._

/**
 * @author Vincent76;
 */
class GameFieldSpec extends AnyWordSpec with Matchers {
  "Coords" when {
    "new" should {
      "have a size" in {
        val test = Coords.all.red( 0, ( i:Int, r:Vector[Option[Hex]] ) => r.red( i, ( j:Int, h:Option[Hex] ) => j + ( if( h .isDefined ) 1 else 0 ) ) )
        test should be( 37 )
      }
      "have an edge" in {

      }
    }
  }
}
