// Copyright (C) 2011-2012 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package fdp.examples.flink

import java.util.concurrent.TimeUnit

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer09, FlinkKafkaProducer09}
import org.apache.flink.streaming.util.serialization.SimpleStringSchema

object Kafka2Kafka {

  import WordCount._

  val stopWords = Set("a", "an", "the")
  val window = Time.of(10, TimeUnit.SECONDS)

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.createLocalEnvironment()

    val kafkaConsumerProperties = Map(
      "zookeeper.connect" -> "localhost:2181",
      "group.id" -> "flink",
      "bootstrap.servers" -> "localhost:9092"
    )

    val kafkaConsumer = new FlinkKafkaConsumer09[String](
      "topic",
      new SimpleStringSchema(),
      kafkaConsumerProperties
    )

    val kafkaProducer = new FlinkKafkaProducer09[String](
      "localhost:9092",
      "output",
      new SimpleStringSchema()
    )

    val lines = env.addSource(kafkaConsumer)

    val wordCounts = countWords(lines, stopWords, window)

    wordCounts
      .map(_.toString)
      .addSink(kafkaProducer)

    env.execute()
  }

  implicit def map2Properties(map: Map[String, String]): java.util.Properties = {
    (new java.util.Properties /: map) { case (props, (k, v)) => props.put(k, v); props }
  }

}

object WordCount {

  type WordCount = (String, Int)

  def countWords(lines: DataStream[String], stopWords: Set[String], window: Time): DataStream[WordCount] = {
    lines
      .flatMap(line => line.split(" "))
      .filter(word => !word.isEmpty)
      .map(word => word.toLowerCase)
      .filter(word => !stopWords.contains(word))
      .map(word => (word, 1))
      .keyBy(0)
      .timeWindow(window)
      .sum(1)
  }

}
