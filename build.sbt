name := "SE-Project"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12"

libraryDependencies += "com.google.inject" % "guice" % "5.0.1"
libraryDependencies += "com.google.inject.extensions" % "guice-assistedinject" % "5.0.1"
libraryDependencies += "net.codingwell" %% "scala-guice" % "5.0.1"

coverageExcludedPackages := "de.htwg.se.settlers.aview.*"
coverageExcludedFiles := ".*Catan"

lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux") => "linux"
  case n if n.startsWith("Mac") => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}

lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
libraryDependencies ++= javaFXModules.map( m=>
  "org.openjfx" % s"javafx-$m" % "15.0.1" classifier osName
)