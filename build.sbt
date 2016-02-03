name := """gnip-rule-parser"""

version := "1.0"

scalaVersion := "2.11.6"

//uncomment the following line if you want cross build
// crossScalaVersions := Seq("2.10.4", "2.11.6")

scalacOptions ++=  Seq(
  "-deprecation",
  "-unchecked",
  "-feature"
)

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

scalariformSettings

//uncomment the following line if you want a java app packaging
// enablePlugins(JavaAppPackaging)
// enablePlugins(UniversalPlugin)
