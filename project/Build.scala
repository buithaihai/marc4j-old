import sbt._
import Keys._
import Build.data

object Marc4jBuild extends Build {
  lazy val marc4jCore = Project(
    id = "marc4j-core",
    base = file("core"),
    settings = commonSettings ++ Seq(
      moduleName := "marc4j",
      libraryDependencies <++= scalaVersion { sv => Seq(
        "com.ibm.icu" % "icu4j" % "2.6.1",
        "com.novocode" % "junit-interface" % "0.8" % "test"
      )}
    )
  )

  lazy val marc4jSamples = Project(
    id = "marc4j-samples",
    base = file("samples"),
    dependencies = Seq(marc4jCore),
    settings = commonSettings
  )

  def commonSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.marc4j",
    version := "3.0.0-SNAPSHOT",
    scalaVersion := "2.10.2",
    scalaBinaryVersion := "2.10.2",
    scalacOptions := Seq(
      "-feature",
      "-language:implicitConversions",
      "-deprecation",
      "-unchecked"
    )
  )
}

