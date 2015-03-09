import AssemblyKeys._
import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

name := "portfolioServiceSimple"

version := "0.1-SNAPSHOT"

organization := "com.gft"

scalaVersion := "2.11.1"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                  "Sonatype snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots/",
                  "Spray Repository"    at "http://repo.spray.io",
                  "Spray Nightlies"     at "http://nightlies.spray.io/")

libraryDependencies ++= {
  val akkaVersion       = "2.3.4"
  val sprayVersion      = "1.3.1"
  Seq(
    "com.typesafe.akka"       %%  "akka-actor"                     % akkaVersion,
    "com.typesafe.akka"       %%  "akka-slf4j"                     % akkaVersion,
    "com.typesafe.akka"       %%  "akka-remote"                    % akkaVersion,
    "com.typesafe.akka"       %%  "akka-multi-node-testkit"        % akkaVersion   % "test",
    "com.typesafe.akka"       %%  "akka-testkit"                   % akkaVersion   % "test",
    "org.scalatest"           %% "scalatest"                       % "2.2.0"       % "test",
    "io.spray"                %% "spray-can"                       % sprayVersion,
    "io.spray"                %% "spray-routing"                   % sprayVersion,
    "io.spray"                %% "spray-json"                      % "1.2.6",
    "com.typesafe.akka"       %% "akka-slf4j"                      % akkaVersion,
    "ch.qos.logback"          %  "logback-classic"                 % "1.1.2"
  )
}

// Assembly settings
mainClass in Global := Some("com.gft.Main")

jarName in assembly := "portfolioServiceSimple-server.jar"

assemblySettings
