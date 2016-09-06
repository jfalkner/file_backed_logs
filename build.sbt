name := "file_backed_logs"

version in ThisBuild := "0.0.2"

organization in ThisBuild := "jfalkner"

scalaVersion in ThisBuild := "2.11.8"

scalacOptions in ThisBuild := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature", "-language:postfixOps")

parallelExecution in ThisBuild := false

fork in ThisBuild := true

// passed to JVM
javaOptions in ThisBuild += "-Xms256m"
javaOptions in ThisBuild += "-Xmx2g"

// `sbt run` will run this class
mainClass in (Compile, run) := Some("falkner.jayson.logs.Main")

libraryDependencies ++= Seq(
  // API needed for Csv -- default serialization for this project
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "commons-io" %  "commons-io" % "2.5",
  // needed only for running the tests
  "org.specs2" % "specs2_2.11" % "2.4.1-scalaz-7.0.6" % "test"
)

lazy val g = RootProject(uri("https://github.com/jfalkner/cc2csv.git#v0.0.4"))
lazy val root = project in file(".") dependsOn g

// allow code coverage via - https://github.com/scoverage/sbt-scoverage
//coverageEnabled := true
//coverageExcludedPackages := "<empty>;.*Export.*AsCSV.*" // don't cover the Util classes -- they should move to a branch
