name := "ScalaMeetup"

version := "0.1"

scalaVersion := "2.12.8"

lazy val akkaHttpVersion = "10.1.8"
lazy val igniteVersion = "2.7.0"
lazy val akkaVersion = "2.5.22"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "org.apache.ignite" % "ignite-core" % igniteVersion,
  "org.apache.ignite" % "ignite-indexing" % igniteVersion
)
