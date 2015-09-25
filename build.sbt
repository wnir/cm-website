name := """cm-website"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

scalaVersion := "2.11.1"

libraryDependencies += "io.prismic" % "java-kit" % "1.1.1"

libraryDependencies += javaWs

libraryDependencies += filters

libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.11.0"

libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23"

TwirlKeys.templateImports += "controllers.Prismic._"

