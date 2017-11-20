name := "grab-etl-v3io"

organization := "io.iguaz"

version := "0.1"

scalaVersion := "2.11.8"

scalacOptions += "-target:jvm-1.7"

//libraryDependencies += "joda-time" % "joda-time" % "2.9.9"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11" % "provided"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.1.1" % "provided"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
