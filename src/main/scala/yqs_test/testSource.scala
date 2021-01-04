import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala._

import scala.util.Random

object testSource {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    //从集合读取数据
    /*
    val sensorDS  = env.fromCollection(
      List(
        WaterSensor("ws_001", 1577844001, 45.0),
        WaterSensor("ws_002", 1577844015, 43.0),
        WaterSensor("ws_003", 1577844020, 42.0)
      )
    ).print()

    val dataStream: DataStream[(String, Long, Double)] =
      env.fromElements(
        ("0001", 0L, 121.2),
        ("0002" ,1L, 201.8),
        ("0003", 2L, 10.3),
        ("0004", 3L, 99.6)
       ).print()
    */

    //从文件中读取数据
    //val fileDS = env.readTextFile("/Users/ks-yangqingsong/Documents/git/ks_ad_flink_demo/demo/src/main/scala/Demo.java").print()

    //kafka读取数据
    /*
    <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-connector-kafka-0.11 -->
      <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-connector-kafka-0.11_2.11</artifactId>
        <version>1.10.0</version>
      </dependency>
    */
    /*
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "hadoop02:9092")
    properties.setProperty("group.id", "consumer-group")
    properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("auto.offset.reset", "latest")

    val kafkaDS: DataStream[String] = env.addSource(
      new FlinkKafkaConsumer011[String](
        "sensor",
        new SimpleStringSchema(),
        properties)
    ).print()
     */

    //自定数据源
    //env.addSource(new MySensorSource).print()

    //控制台
    env.socketTextStream("localhost", 9999).print()

    env.execute("source")
  }

  case class WaterSensor(id: String, ts: Long, vc: Double)

  class MySensorSource extends SourceFunction[WaterSensor] {
    var flg = true
    override def run(ctx: SourceFunction.SourceContext[WaterSensor]): Unit = {
      while ( flg ) {
        // 采集数据
        ctx.collect(
          WaterSensor(
            "sensor_" +new Random().nextInt(3),
            1577844001,
            new Random().nextInt(5)+40
          )
        )
        Thread.sleep(100)
      }
    }

    override def cancel(): Unit = {
      flg = false;
    }
  }
}
