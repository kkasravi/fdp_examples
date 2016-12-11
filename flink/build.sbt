name := "fdp-examples-flink"

version := "1.0"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.flink" % "flink-scala_2.11" % "1.0.0",
  "org.apache.flink" % "flink-clients_2.11" % "1.0.0",
  "org.apache.flink" % "flink-streaming-scala_2.11" % "1.0.0",
  "org.apache.flink" % "flink-connector-kafka-0.9_2.11" % "1.0.0"
)
    
