package de.htwg.se.munchkin

import java.util.function.{ Predicate, UnaryOperator }

import scala.reflect.ClassTag

/**
 * @author Vincent76;
 */

package object util {
  implicit class RichAny[T]( val value:T ) {
    def check( check:Predicate[T], alt:T ):T = if( check.test( value ) ) value else alt
    def use[R]( operation:Function[T, R] ):R = operation.apply( value )
  }

  implicit class ModInt( val value:Int ) {
    def toDisplay:String = {
      if ( value >= 0 ) {
        "+" + ( if ( value < Int.MaxValue ) value else "∞" )
      } else
        value.toString
    }

    def toFullDisplay:String = toFullDisplay()
    def toFullDisplay( prefix:String = "" ):String = if ( value != 0 ) "[" + prefix + ": " + toDisplay + "]" else ""
  }

  implicit class RichString( s:String ) {
    def tab:String = tab()
    def tab( tabs:Int = 1 ):String = {
      ( 1 to 10 ).red( s, ( s:String, _ ) => s.replace( "\n", "\n\t" ) )
    }

    def toLength( length:Int ):String = if( s.length > length ) s.substring( 0, length ) else s.space( length - s.length )

    def space:String = space()
    def space( spaces:Int = 1 ):String = (1 to spaces).red( s, ( s:String, _:Int ) => s + " " )
  }

  implicit class RichIterable[A, B[A] <: Iterable[A]]( iterable:B[A] ) {
    def withType[T <: A :ClassTag]( implicit ct:ClassTag[T] ):B[T] = {
      iterable.filter {
        case e if ct.runtimeClass.isAssignableFrom( e.getClass ) => true
        case _ => false
      }.asInstanceOf[B[T]]
    }

    def red[E]( e:E, action:AppendListElementAction[E, A] ):E = appendListElement( e, iterable.iterator, action )
  }

  implicit class RichStringIterable[T <: String]( iterable:Iterable[T] ) {
    def sumLength:Int = iterable.red( 0, ( v:Int, s:T ) => v + s.length )
  }

  implicit class RichSequence[A]( seq:Seq[A] ) {
    def red[E]( e:E, action:AppendListElementAction[E, A] ):E = appendListElement( e, seq.reverseIterator, action )
  }

  implicit class RichMap[A, B]( map:Map[A, B] ) {
    def red[E]( e:E, action:AppendMapElementAction[E, A, B] ):E = appendMapElement( e, map.iterator, action )
  }

  private def appendListElement[E, A]( e:E, iterator:Iterator[A], action:AppendListElementAction[E, A] ):E = {
    if ( iterator.hasNext ) {
      val next = iterator.next()
      return action.action( appendListElement( e, iterator, action ), next )
    }
    e
  }

  private def appendMapElement[E, A, B]( e:E, iterator:Iterator[(A, B)], action:AppendMapElementAction[E, A, B] ):E = {
    if ( iterator.hasNext ) {
      val (a, b) = iterator.next()
      return action.action( appendMapElement( e, iterator, action ), a, b )
    }
    e
  }

  trait AppendListElementAction[E, A] {
    def action( e:E, a:A ):E
  }

  trait AppendMapElementAction[E, A, B] {
    def action( e:E, a:A, b:B ):E
  }

}

object Util {
  private var cardIDCounter = 0

  def id:Int = {
    cardIDCounter += 1
    cardIDCounter
  }
}



