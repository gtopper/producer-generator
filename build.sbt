name := "grab-etl"

organization := "io.iguaz"

version := "0.1"

scalaVersion := "2.12.4"

scalacOptions += "-target:jvm-1.8"

//libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "joda-time" % "joda-time" % "2.9.9"

libraryDependencies += "com.typesafe" % "config" % "1.3.1"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.10"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies += "com.typesafe.play" % "play-json_2.12" % "2.6.7"
