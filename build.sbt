import ReleaseTransformations._

name := """gnip-rule-validator"""

scalaVersion := "2.11.7"

packageSummary := "Gnip Rule Validator"

packageDescription := "Gnip Rule Validator using FastParse"

maintainer := "Jeroen Rosenberg <jeroen.rosenberg@gmail.com>"

organization := "com.github.jeroenr"

//uncomment the following line if you want cross build
// crossScalaVersions := Seq("2.10.4", "2.11.6")

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

releasePublishArtifactsAction := PgpKeys.publishSigned.value

pomIncludeRepository := { _ => false }

pomExtra :=
  <url>https://github.com/jeroenr/gnip-rule-validator</url>
    <licenses>
      <license>
        <name>BSD-style</name>
        <url>http://www.opensource.org/licenses/bsd-license.php</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:jeroenr/gnip-rule-validator.git</url>
      <connection>scm:git:git@github.com:jeroenr/gnip-rule-validator.git</connection>
    </scm>
    <developers>
      <developer>
        <id>jeroenr</id>
        <name>Jeroen Rosenberg</name>
      </developer>
    </developers>

scalacOptions ++=  Seq(
  "-deprecation",
  "-unchecked",
  "-feature"
)

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "0.3.4",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

scalariformSettings

//uncomment the following line if you want a java app packaging
// enablePlugins(JavaAppPackaging)
// enablePlugins(UniversalPlugin)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)
