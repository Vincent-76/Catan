abstract class Test( id:Int, value:String )

case class Test2( id:Int, value:String ) extends Test( id, value )

case class Test3( override val id:Int, override val value:String ) extends Test2( id, value )

val t3 = Test3( 5, "Test" )

val t31 = t3.copy( id = 6 )