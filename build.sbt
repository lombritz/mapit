import play.Project._

name := "mapit"

version := "1.0"

playScalaSettings

libraryDependencies ++= Seq(jdbc, anorm)
