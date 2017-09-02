name := "bln"

version := "0.1"

scalaVersion := "2.11.11"

val akkaVersion = "2.5.4"

resolvers += Resolver.sonatypeRepo("releases")
libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.8.0",

  "org.apache.ignite" % "ignite-core" % "2.1.0",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.9"
)

enablePlugins(DockerPlugin)

dockerfile in docker := {
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("anapsix/alpine-java")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}