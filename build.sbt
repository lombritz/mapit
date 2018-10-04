import play.Project._

name := "mapit"

version := "1.0"

playScalaSettings

libraryDependencies ++= Seq("com.typesafe.slick" %% "slick" % "2.0.1-RC1", jdbc)
