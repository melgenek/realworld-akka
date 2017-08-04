name := "realworld-akka"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.9",
  "com.softwaremill.macwire" %% "macros" % "2.3.0"
)
