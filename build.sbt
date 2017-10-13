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
    "org.apache.spark" % "spark-core_2.11" % "2.2.0",
    "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "2.8.1",
    "org.apache.spark" % "spark-sql_2.11" % "2.2.0",
    /*"org.apache.hadoop" % "hadoop-common" % "2.8.1",*/
    "com.databricks" % "spark-csv_2.10" % "1.5.0",
    javaJdbc,
    cache,
    javaWs)
}

routesGenerator := InjectedRoutesGenerator