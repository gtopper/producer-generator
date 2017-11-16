name := "grab-etl-v3io"

organization := "io.iguaz"

version := "0.1"

scalaVersion := "2.11.8"

scalacOptions += "-target:jvm-1.7"

//libraryDependencies += "joda-time" % "joda-time" % "2.9.9"

libraryDependencies += "com.typesafe" % "config" % "1.3.1"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName.contains("v3io")}
}
