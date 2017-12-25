name := "realworld-akka"

scalaVersion := "2.12.4"

val versions = new {
  val akkaHttp = "10.0.10"
  val slick = "3.2.1"
  val macwire = "2.3.0"
  val scalatest = "3.0.4"
  val jbcrypt = "0.4"
  val jjwt = "0.9.0"
  val mockito = "2.12.0"
  val chimney = "0.1.6"
  val scalaLogging = "3.7.2"
  val logback = "1.2.3"
}

libraryDependencies ++= dependencies ++ testDependencies

val dependencies = Seq(
  "com.typesafe.akka" %% "akka-http" % versions.akkaHttp,
  "com.typesafe.akka" %% "akka-http-spray-json" % versions.akkaHttp,

  "com.typesafe.slick" %% "slick" % versions.slick,
  "com.typesafe.slick" %% "slick-hikaricp" % versions.slick,

  "org.mindrot" % "jbcrypt" % versions.jbcrypt,
  "io.jsonwebtoken" % "jjwt" % versions.jjwt,
  "io.scalaland" %% "chimney" % versions.chimney,

  "ch.qos.logback" % "logback-classic" % versions.logback,
  "com.typesafe.scala-logging" %% "scala-logging" % versions.scalaLogging,
  "com.softwaremill.macwire" %% "macros" % versions.macwire % Provided
)

val testDependencies = Seq(
  "com.typesafe.akka" %% "akka-http-testkit" % versions.akkaHttp,
  "org.mockito" % "mockito-core" % versions.mockito,
  "org.scalatest" %% "scalatest" % versions.scalatest
).map(_ % Test)
