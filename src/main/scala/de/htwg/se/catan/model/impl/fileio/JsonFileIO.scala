package de.htwg.se.catan.model.impl.fileio

import de.htwg.se.catan.model.{ Command, FileIO, Game }
import play.api.libs.json.{ JsArray, JsDefined, JsLookupResult, JsNull, JsValue, Json, Reads, Writes }

import java.io.{ File, PrintWriter }
import scala.io.Source

trait JsonSerializable:
  def toJson:JsValue


given jsonSerializableWrites:Writes[JsonSerializable] = ( o:JsonSerializable ) => o.toJson


trait JsonDeserializer[+T]:
  def fromJson( json:JsValue ):T


case class JsonParseError( expected:String, got:String ) extends RuntimeException:
  override def toString:String = "JsonParseError: Expected -> '" + expected + "', Got -> '" + got + "'"


object JsonFileIO extends FileIO( "json" ):

  override def load( path:String ):(Game, List[Command], List[Command]) =
    val source = Source.fromFile( path )
    val json = Json.parse( source.getLines().mkString )
    val game = ( json \ "game" ).as[Game]
    val undoStack = ( json \ "undoStack" ).asList[Command]
    val redoStack = ( json \ "redoStack" ).asList[Command]
    source.close()
    (game, undoStack, redoStack)

  override def save( game:Game, undoStack:List[Command], redoStack:List[Command] ):String =
    val file = File( getFileName )
    val pw = PrintWriter( file )
    val save = Json.obj(
      "game" -> Json.toJson( game ),
      "undoStack" -> Json.toJson( undoStack ),
      "redoStack" -> Json.toJson( redoStack )
    )
    pw.write( Json.prettyPrint( save ) )
    pw.close()
    file.getAbsolutePath


  implicit class JsonSeq[E]( seq:Seq[E] ):
    def toJson( using jfs:Writes[E] ):JsArray =
      JsArray( seq.map( e => Json.toJson( e ) ) ) //toJson( e => Json.toJson( e ) )

    def toJsonC( builder:E => JsValue ):JsArray =
      JsArray( seq.map( builder ) )

  implicit class JsonMap[K, V]( map:Map[K, V] ):
    def toJson( using fjs:Writes[K], fjs2:Writes[V] ):JsArray =
      JsArray( map.map( d => JsArray( Seq( Json.toJson( d._1 ), Json.toJson( d._2 ) ) ) ).toSeq )//map.toJson( k => Json.toJson( k ), v => Json.toJson( v ) )

    def toJsonC( keyBuilder:K => JsValue, valBuilder:V => JsValue ):JsArray =
      JsArray( map.map( d => JsArray( Seq( keyBuilder( d._1 ), valBuilder( d._2 ) ) ) ).toSeq )


  implicit class JsonTuple2[T1, T2]( tuple:(T1, T2) ):
    def toJson( using fjs:Writes[T1], fjs2:Writes[T2] ):JsValue =
      JsArray( Seq( Json.toJson( tuple._1 ), Json.toJson( tuple._2) ) )//tuple.toJson( v1 => Json.toJson( v1 ), v2 => Json.toJson( v2 ) )

    def toJsonC( builder1:T1 => JsValue, builder2:T2 => JsValue ):JsValue =
      JsArray( Seq( builder1( tuple._1 ), builder2( tuple._2) ) )


  implicit class JsonTuple3[T1, T2, T3]( tuple:(T1, T2, T3) ):
    def toJson( using fjs:Writes[T1], fjs2:Writes[T2], fjs3:Writes[T3] ):JsValue =
      JsArray( Seq( Json.toJson( tuple._1 ), Json.toJson( tuple._2 ), Json.toJson( tuple._3 ) ) )//tuple.toJson( v1 => Json.toJson( v1 ), v2 => Json.toJson( v2 ), v3 => Json.toJson( v3 ) )

    def toJsonC( builder1:T1 => JsValue, builder2:T2 => JsValue, builder3:T3 => JsValue ):JsValue =
      JsArray( Seq( builder1( tuple._1 ), builder2( tuple._2 ), builder3( tuple._3 ) ) )


  implicit class JsonValue( json:JsValue ):
    def asOption[E]( using fjs:Reads[E] ):Option[E] = asOptionC( _.as[E] )

    def asOptionC[E]( builder:JsValue => E ):Option[E] = json match
      case JsNull => None
      case a:JsArray if a.value.isEmpty => None
      case v => Some( builder( v ) )

    private def asIndexedSeq[E]( builder:JsValue => E ):scala.collection.IndexedSeq[E] = json match
      case arr:JsArray => arr.value.map( builder )
      case o => throw JsonParseError( expected = "JsonArray", got = o.toString() )

    def asSeq[E]( using fjs:Reads[E] ):Seq[E] = asSeqC( _.as[E] )

    def asSeqC[E]( builder:JsValue => E ):Seq[E] = asIndexedSeq( builder ).toSeq

    def asList[E]( using fjs:Reads[E] ):List[E] = asListC( _.as[E] )

    def asListC[E]( builder:JsValue => E ):List[E] = asIndexedSeq( builder ).toList

    def asVector[E]( using fjs:Reads[E] ):Vector[E] = asVectorC( _.as[E] )

    def asVectorC[E]( builder:JsValue => E ):Vector[E] = asIndexedSeq( builder ).toVector


    def asMap[K, V]( using fjs:Reads[K], fjs2:Reads[V] ):Map[K, V] = asMapC[K, V]( _.as[K], _.as[V] )

    def asMapC[K, V]( keyBuilder:JsValue => K, valBuilder:JsValue => V ):Map[K, V] = json match
      case arr:JsArray => arr.value.map {
          case valArr:JsArray => (keyBuilder( valArr( 0 ) ), valBuilder( valArr( 1 ) ))
          case o => throw JsonParseError( expected = "JsonArray", got = o.toString() )
        }.toMap
      case o => throw JsonParseError( expected = "JsonArray", got = o.toString() )

    def asTuple[T1, T2]( using fjs:Reads[T1], fjs2:Reads[T2] ):(T1, T2) = asTupleC( _.as[T1], _.as[T2] )

    def asTupleC[T1, T2]( builder1:JsValue => T1, builder2:JsValue => T2 ):(T1, T2) = json match
      case arr:JsArray => (builder1( arr( 0 ) ), builder2( arr( 1 ) ))
      case o => throw JsonParseError( expected = "JsonArray", got = o.toString() )

    def asTuple[T1, T2, T3]( using fjs:Reads[T1], fjs2:Reads[T2], fjs3:Reads[T3] ):(T1, T2, T3) = asTupleC( _.as[T1], _.as[T2], _.as[T3] )

    def asTupleC[T1, T2, T3]( builder1:JsValue => T1, builder2:JsValue => T2, builder3:JsValue => T3 ):(T1, T2, T3) = json match
      case arr:JsArray => (builder1( arr( 0 ) ), builder2( arr( 1 ) ), builder3( arr( 2 ) ))
      case o => throw JsonParseError( expected = "JsonArray", got = o.toString() )


  implicit class JsonLookupResult( jsonRes:JsLookupResult ):
    def asOption[E]( using fjs: Reads[E] ):Option[E] = asOptionC( _.as[E] )

    def asOptionC[E]( builder:JsValue => E ):Option[E] = jsonRes match
      case JsDefined( value ) => value.asOptionC( builder )
      case _ => None

    def asSeq[E]( using fjs:Reads[E] ):Seq[E] = jsonRes.get.asSeq[E]

    def asSeqC[E]( builder:JsValue => E ):Seq[E] = jsonRes.get.asSeqC( builder )

    def asList[E]( using fjs:Reads[E] ):List[E] = jsonRes.get.asList[E]

    def asListC[E]( builder:JsValue => E ):List[E] = jsonRes.get.asListC( builder )

    def asVector[E]( using fjs:Reads[E] ):Vector[E] = jsonRes.get.asVector[E]

    def asVectorC[E]( builder:JsValue => E ):Vector[E] = jsonRes.get.asVectorC( builder )

    def asMap[K, V]( using fjs:Reads[K], fjs2:Reads[V] ):Map[K, V] = jsonRes.get.asMap[K, V]

    def asMapC[K, V]( keyBuilder:JsValue => K, valBuilder:JsValue => V ):Map[K, V] =
      jsonRes.get.asMapC( keyBuilder, valBuilder )

    def asTuple[T1, T2]( using fjs:Reads[T1], fjs2:Reads[T2] ):(T1, T2) = jsonRes.get.asTuple[T1, T2]

    def asTupleC[T1, T2]( builder1:JsValue => T1, builder2:JsValue => T2 ):(T1, T2) = jsonRes.get.asTupleC( builder1, builder2 )

    def asTuple[T1, T2, T3]( using fjs:Reads[T1], fjs2:Reads[T2], fjs3:Reads[T3] ):(T1, T2, T3) = jsonRes.get.asTuple[T1, T2, T3]

    def asTupleC[T1, T2, T3]( builder1:JsValue => T1, builder2:JsValue => T2, builder3:JsValue => T3 ):(T1, T2, T3) = jsonRes.get.asTupleC( builder1, builder2, builder3 )
