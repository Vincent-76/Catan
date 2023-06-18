name := "Catan"
version := "1.0"
organization := "de.htwg.se.catan"

val scala2Version = "2.13.8"
val scala3Version = "3.2.2"

val model = project.in( file( "de.htwg.se.catan.model" ) )
val util = project.in( file( "de.htwg.se.catan.util" ) )
val tui = project.in( file( "de.htwg.se.catan.aview.tui" ) )
val gui = project.in( file( "de.htwg.se.catan.aview.gui" ) )

lazy val root = project
  .in( file(".") )
  .settings(
    /*name := "scala3-cross",
    version := "0.1.0",*/
    Compile / packageBin / mainClass := Some( "de.htwg.se.catan.Catan" ),
    Compile / run / mainClass := Some( "de.htwg.se.catan.Catan" ),

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.scalactic" %% "scalactic" % "3.2.15",
      "org.scalatest" %% "scalatest" % "3.2.15" % "test",
      ( "org.scalafx" %% "scalafx" % "20.0.0-R31" )
        .exclude( "org.scala-lang.modules", "scala-collection-compat_2.13" ),

      "com.google.inject" % "guice" % "5.1.0",
      "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
      ( "net.codingwell" %% "scala-guice" % "5.0.2" ).cross( CrossVersion.for3Use2_13 ),

      "org.scala-lang.modules" %% "scala-xml" % "2.0.1",
      //( "com.typesafe.play" %% "play-json" % "2.10.0-RC1" ).cross( CrossVersion.for3Use2_13 ),
      ( "com.typesafe.play" %% "play-json" % "2.10.0-RC1" )
        .cross( CrossVersion.for3Use2_13 )
        .exclude( "org.scala-lang.modules", "scala-collection-compat_2.13" ),


      ( "com.typesafe.akka" %% "akka-http" % "10.2.9" ).cross( CrossVersion.for3Use2_13 ),
      ( "com.typesafe.akka" %% "akka-actor-typed" % "2.6.19" ).cross( CrossVersion.for3Use2_13 ),
      ( "com.typesafe.akka" %% "akka-stream" % "2.6.19" ).cross( CrossVersion.for3Use2_13 ),
      "com.github.slick.slick" % "slick_3" % "nafg~dottyquery-SNAPSHOT",
      "mysql" % "mysql-connector-java" % "8.0.29",
      ( "org.mongodb.scala" %% "mongo-scala-driver" % "4.6.0" ).cross( CrossVersion.for3Use2_13 ),
    ).map( _.exclude( "org.scala-lang.modules", "scala-collection-compat_2.13") ),

    /*libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,


    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.15",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test",
    libraryDependencies += ( "org.scalafx" %% "scalafx" % "17.0.1-R26" )
      .exclude("org.scala-lang.modules", "scala-collection-compat_2.13"),

    libraryDependencies += "com.google.inject" % "guice" % "5.1.0",
    libraryDependencies += "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
    libraryDependencies += ( "net.codingwell" %% "scala-guice" % "5.0.2" ).cross( CrossVersion.for3Use2_13 ),

    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.0.1",
    //libraryDependencies += ( "com.typesafe.play" %% "play-json" % "2.10.0-RC1" ).cross( CrossVersion.for3Use2_13 ),
    libraryDependencies += ( "com.typesafe.play" %% "play-json" % "2.10.0-RC1" )
      .cross( CrossVersion.for3Use2_13 )
      .exclude( "org.scala-lang.modules", "scala-collection-compat_2.13" ),


      libraryDependencies += ( "com.typesafe.akka" %% "akka-http" % "10.2.9" ).cross( CrossVersion.for3Use2_13 ),
    libraryDependencies += ( "com.typesafe.akka" %% "akka-actor-typed" % "2.6.19" ).cross( CrossVersion.for3Use2_13 ),
    libraryDependencies += ( "com.typesafe.akka" %% "akka-stream" % "2.6.19" ).cross( CrossVersion.for3Use2_13 ),
    libraryDependencies += "com.github.slick.slick" % "slick_3" % "nafg~dottyquery-SNAPSHOT",
    libraryDependencies +="mysql" % "mysql-connector-java" % "8.0.29",
    libraryDependencies += ( "org.mongodb.scala" %% "mongo-scala-driver" % "4.6.0" ).cross( CrossVersion.for3Use2_13 ),
    //libraryDependencies += ( "ch.qos.logback" % "logback-classic" % "1.2.11" ).cross( CrossVersion.for3Use2_13 ),
    /*libraryDependencies ++= Seq(
      ( "com.typesafe.slick" %% "slick" % "3.3.3" ).cross( CrossVersion.for3Use2_13 ),
      "ch.qos.logback" % "logback-classic" % "1.2.11",
      // "org.slf4j" % "slf4j-nop" % "1.6.4",
    ),*/*/

    dependencyOverrides += "org.scala-lang.modules" %% "scala-collection-compat" % "2.5.0",


    resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/",
    resolvers += ( "Typesafe Simple Repository" at "http://repo.typesafe.com/typesafe/simple/maven-releases/" ).withAllowInsecureProtocol( true ),
    resolvers += "jitpack" at "https://jitpack.io",

    coverageExcludedPackages := "de.htwg.se.catan.aview.*",
    coverageExcludedFiles := ".*(Catan|CatanModule|Requests)",

    // To make the default compiler and REPL use Dotty
    scalaVersion := scala3Version,
    // To cross compile with Scala 3 and Scala 2
    crossScalaVersions := Seq( scala3Version, scala2Version ),

    assembly / mainClass := Some( "de.htwg.se.catan.Catan" ),
    assembly / assemblyJarName := "CatanLib.jar",
    assembly / assemblyMergeStrategy := {
      case "reference.conf" => MergeStrategy.concat
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )/*.dependsOn( `slick` )*/.aggregate( model, util, tui )

/*lazy val `slick` = project.in( file( "de.htwg.se.catan.model.impl.slick" ) ).settings(
  scalaVersion := scala2Version,
  libraryDependencies ++= Seq(
    "com.typesafe.slick" %% "slick" % "3.3.3",
    "ch.qos.logback" % "logback-classic" % "1.2.11",
    // "org.slf4j" % "slf4j-nop" % "1.6.4",
  ),
  resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/",
  resolvers += ( "Typesafe Simple Repository" at "http://repo.typesafe.com/typesafe/simple/maven-releases/" ).withAllowInsecureProtocol( true ),
).aggregate( model, util )*/

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
  case _ => throw new Exception("Unknown platform!")
}

lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
libraryDependencies ++= javaFXModules.map( m=>
  "org.openjfx" % s"javafx-$m" % "16" classifier osName
)