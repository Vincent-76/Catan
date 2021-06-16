package de.htwg.se.catan

import java.util.function.Predicate

import de.htwg.se.catan.model.Cards.ResourceCards
import de.htwg.se.catan.model.{ InsufficientResources, Resource, Resources }

import scala.reflect.ClassTag
import scala.util.{ Failure, Random, Success, Try }

/**
 * @author Vincent76;
 */

package object util {

  implicit class RichAny[T]( val value:T ) {
    def check( check:Predicate[T] ):Boolean = check.test( value )

    def validate( validator:Predicate[T], alt:T ):T = if ( validator.test( value ) ) value else alt

    def use[R]( operation:Function[T, R] ):R = operation.apply( value )
  }

  implicit class RichInt( val value:Int ) {
    def toLength( length:Int ):String = {
      val s = value.toString
      val newLength = if ( length < s.length ) s.length else length
      "".toLength( newLength - s.length ) + s
    }
  }

  implicit class RichString( s:String ) {
    def tab:String = tab()

    def tab( tabs:Int = 1 ):String = {
      ( 1 to tabs ).red( s, ( s:String, _:Int ) => s.replace( "\n", "\n\t" ) )
    }

    def toLength( length:Int ):String = if ( s.length > length ) s.substring( 0, length ) else s.space( length - s.length )

    def space:String = space()

    def space( spaces:Int = 1 ):String = ( 1 to spaces ).red( s, ( s:String, _:Int ) => s + " " )

    def removeSpaces( ):String = s.replaceAll( "\\s+", "" )

    def =^( c:String ):Boolean = s.toLowerCase == c.toLowerCase
  }

  implicit class RichRandom( r:Random ) {
    def element[R]( i:Seq[R] ):Option[R] = if ( i.nonEmpty ) Some( i( r.nextInt( i.size ) ) ) else None
  }

  implicit class RichIterable[A, B[A] <: Iterable[A]]( iterable:B[A] ) {
    def withType[T <: A :ClassTag]( implicit ct:ClassTag[T] ):B[T] = {
      val n = iterable.filter {
        case e if ct.runtimeClass.isAssignableFrom( e.getClass ) => true
        case _ => false
      }
      n.asInstanceOf[B[T]]
    }

    def red[E]( e:E, action:AppendListElementAction[E, A] ):E = appendListElement( e, iterable.iterator, action )

    def redByKey[E]( e:E, action:AppendListElementByKeyAction[E] ):E = appendListElementByKey( e, iterable.size - 1, action )

    def removeAt( i:Int ):B[A] = {
      iterable.splitAt( i ).use( d => d._1 ++ d._2.tail ).asInstanceOf[B[A]]
    }

    def containsWhere( p: A => Boolean ):Boolean = {
      iterable.foreach( e => {
        if( p( e ) )
          return true
      } )
      false
    }
  }

  implicit class RichGeneralIterable( val iterable:Iterable[_] ) {
    def deepFind[R]( predicate:Predicate[R] )( implicit ct:ClassTag[R] ):Option[R] = {
      iterable.foreach {
        case e if ct.runtimeClass.isAssignableFrom( e.getClass ) =>
          if ( predicate.test( e.asInstanceOf[R] ) )
            return Some( e.asInstanceOf[R] )
        case i:Iterable[_] => val res = i.deepFind( predicate )
          if ( res.isDefined )
            return res
      }
      None
    }
  }

  implicit class RichStringIterable[T <: String]( iterable:Iterable[T] ) {
    def sumLength:Int = iterable.red( 0, ( i:Int, s:String ) => i + s.length )
  }

  implicit class RichSequence[A]( seq:Seq[A] ) {
    def red[E]( e:E, action:AppendListElementAction[E, A] ):E = appendListElement( e, seq.reverseIterator, action )

    def redByKey[E]( e:E, action:AppendListElementByKeyAction[E] ):E = appendListElementByKey( e, seq.size - 1, action )

    def removed( e:A ):Seq[A] = seq.indexOf( e ) match {
      case -1 => seq
      case i:Int => seq.removeAt( i )
    }

    def sortBySeq( sortSeq:Seq[A] ):Seq[A] = {
      val sorted = sortSeq.flatMap( e => seq.filter( _ == e ) )
      sorted ++ seq.filter( !sorted.contains( _ ) )
    }
  }

  implicit class RichMap[A, B]( map:Map[A, B] ) {
    def red[E]( e:E, action:AppendMapElementAction[E, A, B] ):E = appendMapElement( e, map.iterator, action )

    def sortBySeq( sortSeq:Seq[A] ):Seq[(A, B)] = sortSeq.zip( sortSeq.map( map ) )
  }

  private def appendListElement[E, A]( e:E, iterator:Iterator[A], action:AppendListElementAction[E, A] ):E = {
    if ( iterator.hasNext ) {
      val next = iterator.next()
      return action.action( appendListElement( e, iterator, action ), next )
    }
    e
  }

  private def appendListElementByKey[E]( e:E, i:Int, action:AppendListElementByKeyAction[E] ):E = {
    if ( i >= 0 )
      return action.action( appendListElementByKey( e, i - 1, action ), i )
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

  trait AppendListElementByKeyAction[E] extends AppendListElementAction[E, Int]

  trait AppendMapElementAction[E, A, B] {
    def action( e:E, a:A, b:B ):E
  }

  implicit class RichMatrix[E]( val iterable:Iterable[Iterable[E]] ) {
    def matrixFind( predicate:Predicate[E] ):Option[E] = {
      iterable.foreach( _.foreach( e => if ( predicate.test( e ) ) return Some( e ) ) )
      None
    }
  }

  implicit class RichOption[T]( val option:Option[T] ) {
    def useOrElse[R]( operation:Function[T, R], default:R ):R = if( option.isDefined ) operation.apply( option.get ) else default
  }

  implicit class RichTry[T]( t:Try[T] ) {
    def throwable:Throwable = t match {
      case Failure( e ) => e
      case _ => throw new NullPointerException
    }

    def rethrow[B:ClassTag]( implicit ct:ClassTag[B] ):Failure[B] = t match {
      case f:Failure[T] => f.asInstanceOf[Failure[B]]
      case _ => throw new NullPointerException
    }

    def failureOption:Option[Throwable] = t match {
      case Failure( t ) => Some( t )
      case _ => None
    }
  }

}



