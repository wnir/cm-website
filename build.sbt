name := """cm-website"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies += "io.prismic" % "java-kit" % "1.1.1"

libraryDependencies += javaWs

TwirlKeys.templateImports += "controllers.Prismic._"

