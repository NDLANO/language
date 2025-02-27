val scala213 = "2.13.3"
val scala212 = "2.12.10"
val Scalaversion = scala213
val ScalaTestVersion = "3.2.1"
val MockitoVersion = "1.10.19"
val AwsSdkversion = "1.11.93"
val ScalaLoggingVersion = "3.9.2"
val Json4sVersion = "3.6.7"

lazy val supportedScalaVersions = List(
  scala213,
  scala212
)

lazy val commonSettings = Seq(
  organization := "ndla",
  scalaVersion := Scalaversion,
  crossScalaVersions := supportedScalaVersions
)

// Workaround for: https://github.com/sbt/sbt/issues/3570
updateOptions := updateOptions.value.withGigahorse(false)

lazy val language = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "language",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions := Seq("-target:jvm-1.8"),
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-native" % Json4sVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion,
      "org.scalatest" %% "scalatest" % ScalaTestVersion % "test",
      "org.mockito" % "mockito-all" % MockitoVersion % "test"
    )
  )

val checkfmt = taskKey[Boolean]("Check for code style errors")
checkfmt := {
  val noErrorsInMainFiles = (Compile / scalafmtCheck).value
  val noErrorsInTestFiles = (Test / scalafmtCheck).value
  val noErrorsInSbtConfigFiles = (Compile / scalafmtSbtCheck).value

  noErrorsInMainFiles && noErrorsInTestFiles && noErrorsInSbtConfigFiles
}

Test / test := ((Test / test).dependsOn(Test / checkfmt)).value

val fmt = taskKey[Unit]("Automatically apply code style fixes")
fmt := {
  (Compile / scalafmt).value
  (Test / scalafmt).value
  (Compile / scalafmtSbt).value
}

publishTo := {
  val nexus = sys.props.getOrElse("nexus.host", "")
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/ndla-snapshots")
  else
    Some("releases" at nexus + "content/repositories/ndla-releases")
}
