name := "fdp-examples-spark"

version := "1.0"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-streaming_2.11" % "2.0.2",
  "org.apache.spark" % "spark-streaming-kafka-0-8_2.11" % "2.0.2"
)
    
