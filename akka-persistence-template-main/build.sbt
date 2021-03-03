
organization := "kz.dar.tech"

name := "akka-persistence-template"

version := "0.1"

scalaVersion := "2.13.4"

val AkkaVersion = "2.6.10"
val AkkaPersistenceCassandraVersion = "1.0.3"
val AkkaHttpVersion = "10.2.1"
val AkkaProjectionVersion = "1.0.0"
val circeVersion = "0.13.0"
val swaggerVersion = "2.1.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % AkkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra" % AkkaPersistenceCassandraVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % AkkaPersistenceCassandraVersion,

  "com.lightbend.akka" %% "akka-projection-eventsourced" % AkkaProjectionVersion,
  "com.lightbend.akka" %% "akka-projection-cassandra" % AkkaProjectionVersion,

  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,

  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,

  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",

  "de.heikoseeberger" %% "akka-http-circe" % "1.29.1",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.0.4",
  "com.github.swagger-akka-http" %% "swagger-scala-module" % "2.0.6",
  "io.swagger.core.v3" % "swagger-core" % swaggerVersion,
  "io.swagger.core.v3" % "swagger-annotations" % swaggerVersion,
  "io.swagger.core.v3" % "swagger-models" % swaggerVersion,
  "io.swagger.core.v3" % "swagger-jaxrs2" % swaggerVersion,
  "javax.ws.rs" % "javax.ws.rs-api" % "2.0.1",

  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.lightbend.akka" %% "akka-projection-testkit" % AkkaProjectionVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.2" % Test,
  "joda-time" % "joda-time" % "2.10.5",
  "commons-io" % "commons-io" % "2.4" % Test)


scalacOptions in Compile ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")
javacOptions in Compile ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

fork in run := false
parallelExecution in Test := false
logBuffered in Test := false

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

packageName in Universal := "app"

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)