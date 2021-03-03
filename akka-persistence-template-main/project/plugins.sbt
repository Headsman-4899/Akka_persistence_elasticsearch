addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.33")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.10.9"


resolvers += Classpaths.sbtPluginReleases
resolvers += Classpaths.typesafeReleases

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.8")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.12")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.0.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")
