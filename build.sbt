name := "bln"

version := "0.1"

scalaVersion := "2.11.11"

val akkaVersion = "2.5.4"

resolvers += Resolver.sonatypeRepo("releases")
libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.8.0",

  "com.github.rubanm" %% "ignite-scala" % "0.0.1",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.0.10"
)

enablePlugins(DockerPlugin)

dockerfile in docker := {
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("alpine")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}