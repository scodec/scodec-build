import sbtrelease._
import ReleaseStateTransformations._
import ReleasePlugin._
import ReleaseKeys._

organization := "org.scodec"
name := "scodec-build"

crossSbtVersions := Seq("1.2.0")

sbtPlugin := true

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addPlugin("com.typesafe.sbt" % "sbt-osgi" % "0.9.5")
addPlugin("com.github.gseitz" % "sbt-release" % "1.0.12")
addPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.8.1")
addPlugin("io.crashbox" % "sbt-gpg" % "0.2.1")
addPlugin("com.typesafe.sbt" % "sbt-site" % "1.3.3")
addPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.3")
addPlugin("com.typesafe" % "sbt-mima-plugin" % "0.6.4")
addPlugin("pl.project13.scala" % "sbt-jmh" % "0.3.7")
addPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")

// https://github.com/sbt/sbt/pull/3397
def addPlugin(plugin: ModuleID) = {
  libraryDependencies += Defaults.sbtPluginExtra(
    plugin,
    (sbtBinaryVersion in pluginCrossBuild).value,
    (scalaBinaryVersion in pluginCrossBuild).value
  )
}

licenses += ("Three-clause BSD-style", url("https://github.com/scodec/scodec-build/blob/master/LICENSE"))

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { x => false }
pomExtra := (
  <url>http://github.com/scodec/scodec-build</url>
  <scm>
    <url>git@github.com:scodec/scodec-build.git</url>
    <connection>scm:git:git@github.com:scodec/scodec-build.git</connection>
  </scm>
  <developers>
    <developer>
      <id>mpilquist</id>
      <name>Michael Pilquist</name>
      <url>http://github.com/mpilquist</url>
    </developer>
  </developers>
)

import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepCommandAndRemaining("^ test"),
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("^ publish"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
