name := "hello-scala"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.typesafe.slick" %% "slick" % "3.0.2+",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.4.188",
  "com.typesafe" % "config" % "1.3.0+",
  "com.github.seratch" %% "awscala" % "0.5.3+",
  "com.amazon.redshift" %% "redshift" % "1.1.6" from "https://s3.amazonaws.com/redshift-downloads/drivers/RedshiftJDBC41-1.1.6.1006.jar",
  "joda-time" % "joda-time" % "2.8.2"
)
