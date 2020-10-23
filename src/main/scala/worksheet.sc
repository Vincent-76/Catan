class Obj(val1:Int, val2:String) {
  def increment = new Obj( val1 + 1, val2 )

  override def toString: String = {
    val1.toString + val2
  }
}

val obj1 = new Obj(1, "five")
val obj2 = new Obj( 4, "six" )
val obj3 = obj2.increment
val number = 5;
val nNumber = number + 4;
println( obj2 )