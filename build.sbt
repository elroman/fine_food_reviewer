name := "fine_food_reviewer"

version := "1.0"

lazy val root = (project in file("."))
  .enablePlugins(UniversalPlugin, PlayJava, JavaServerAppPackaging)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

scalaVersion := "2.11.8"

mainClass in Compile := Some("play.core.server.ProdServerStart")

libraryDependencies ++= {
  val akkaV = "2.4.16"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,

    javaJdbc,
    cache,
    javaWs)
}

routesGenerator := InjectedRoutesGenerator