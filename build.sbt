name := "ignite"

version := "0.1"

scalaVersion := "2.11.11"

resolvers += Resolver.sonatypeRepo("releases")
libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.8.0",

  "com.github.rubanm" %% "ignite-scala" % "0.0.1",

  "com.typesafe.akka" %% "akka-actor" % "2.5.4",
  "com.typesafe.akka" %% "akka-stream" % "2.5.4",
  "com.typesafe.akka" %% "akka-http" % "10.0.10"
)