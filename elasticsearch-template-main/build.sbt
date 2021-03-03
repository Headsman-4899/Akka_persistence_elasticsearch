
name := "elasticsearch-template"

version := "0.1"

//scalaVersion := "2.13.5"
scalaVersion := "2.12.10"

lazy val AkkaVersion = "2.6.12"
lazy val AkkaHttpVersion = "10.2.3"
lazy val elastic4sVersion = "7.11.0"
lazy val circeVersion = "0.13.0"

libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-json-circe" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-client-akka" % elastic4sVersion,



  "org.scalatest" %% "scalatest" % "3.2.5" % Test,

  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % "test",
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,

  "de.heikoseeberger" %% "akka-http-circe" % "1.29.1",

  "ch.qos.logback" % "logback-classic" % "1.1.8",

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-jawn" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  "javax.ws.rs" % "javax.ws.rs-api" % "2.0.1",

  "joda-time" % "joda-time" % "2.10.5"
)

//packageName in Universal := "app"

//enablePlugins(JavaAppPackaging)