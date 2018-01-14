name := "grab-generator-producer"

organization := "io.iguaz"

version := "0.1"

scalaVersion := "2.11.12"

scalacOptions += "-target:jvm-1.7"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp.filter(_.data.getName.contains("v3io"))
}
