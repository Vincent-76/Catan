name := "Catan"
version := "1.0"

val scala2Version = "2.13.8"
val scala3Version = "3.1.1"

lazy val root = project
  .in( file(".") )
  .settings(
    /*name := "scala3-cross",
    version := "0.1.0",*/

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,


    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.11",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test",
    libraryDependencies += "org.scalafx" %% "scalafx" % "17.0.1-R26",

    libraryDependencies += "com.google.inject" % "guice" % "5.1.0",
    libraryDependencies += "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
    libraryDependencies += ( "net.codingwell" %% "scala-guice" % "5.0.2" ).cross( CrossVersion.for3Use2_13 ),

    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.0.1",
    libraryDependencies += ( "com.typesafe.play" %% "play-json" % "2.10.0-RC1" ).cross( CrossVersion.for3Use2_13 ) ,

    coverageExcludedPackages := "de.htwg.se.catan.aview.*",
    coverageExcludedFiles := ".*(Catan|CatanModule)",

    // To make the default compiler and REPL use Dotty
    scalaVersion := scala3Version,
    // To cross compile with Scala 3 and Scala 2
    crossScalaVersions := Seq( scala3Version, scala2Version )
  )

/*libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.11"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test"
libraryDependencies += "org.scalafx" %% "scalafx" % "17.0.1-R26"

libraryDependencies += "com.google.inject" % "guice" % "5.1.0"
libraryDependencies += "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0"
libraryDependencies += ( "net.codingwell" %% "scala-guice" % "4.2.9" ).cross( CrossVersion.for3Use2_13 )

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.0.1"
libraryDependencies += ( "com.typesafe.play" %% "play-json" % "2.10.0-RC1" ).cross( CrossVersion.for3Use2_13 )

coverageExcludedPackages := "de.htwg.se.catan.aview.*"
coverageExcludedFiles := ".*(Catan|CatanModule)"

// To cross compile with Scala 3 and Scala 2

scalaVersion := scala3Version
crossScalaVersions := Seq( scala3Version, scala2Version )*/

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux") => "linux"
  case n if n.startsWith("Mac") => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw Exception("Unknown platform!")
}

lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
libraryDependencies ++= javaFXModules.map( m=>
  "org.openjfx" % s"javafx-$m" % "16" classifier osName
)




