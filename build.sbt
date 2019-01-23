name := "realworld-akka"

scalaVersion := "2.12.6"

val versions = new {
  val akkaHttp = "10.0.10"
  val slick = "3.2.1"
  val circe = "0.11.1"
}

libraryDependencies ++= dependencies ++ testDependencies


val dependencies = Seq(
  "com.typesafe.akka" %% "akka-http" % versions.akkaHttp,

  "de.heikoseeberger" %% "akka-http-circe" % "1.24.3",
  "io.circe" %% "circe-core" % versions.circe,
  "io.circe" %% "circe-generic" % versions.circe,
  "io.circe" %% "circe-parser" % versions.circe,

  "com.typesafe.slick" %% "slick" % versions.slick,
  "com.typesafe.slick" %% "slick-hikaricp" % versions.slick,
  "com.h2database" % "h2" % "1.4.196",

  "org.mindrot" % "jbcrypt" % "0.4",
  "io.jsonwebtoken" % "jjwt" % "0.9.0",

  "commons-validator" % "commons-validator" % "1.6",
  "io.scalaland" %% "chimney" % "0.1.6",
  "org.typelevel" %% "cats-core" % "1.0.1",

  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % Provided
)

val testDependencies = Seq(
  "com.typesafe.akka" %% "akka-http-testkit" % versions.akkaHttp,
  "org.mockito" % "mockito-core" % "1.10.19",
  "org.scalatest" %% "scalatest" % "3.0.4"
).map(_ % Test)
