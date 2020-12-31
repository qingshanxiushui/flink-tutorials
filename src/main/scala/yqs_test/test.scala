package yqs_test

import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.source.SourceFunction

import scala.util.Random

object test {
  case class AAAAClass(var id: String = "" ,
                       var name: String = "" ,
                       var source: Double = 0.0
                      )
  def main(args: Array[String]): Unit = {
    printf("1111")

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    // exactly-once 语义保证整个应用内端到端的数据一致性
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    // 开启检查点并指定检查点时间间隔为5s
    env.enableCheckpointing(5000) // checkpoint every 5000 msecs
    // 使用自定义SourceFunction
    val data_source = env.addSource(new CustomGenerator())
    data_source.print().setParallelism(1)
    //    data_source.map(r=>r)
    //    data_source.flatMap(row=>row)

    env.execute("Custom Source")

  }

  // 自定义Source，重写SourceFunction两个方法
  class CustomGenerator extends SourceFunction[AAAAClass] {
    private var running = true
    override def run(ctx: SourceFunction.SourceContext[AAAAClass]): Unit = {
      // 随机数生成器
      var randomNum: Random = new Random(1000)

      while (running) {
        // 利用ctx上下文将数据返回
        ctx.collect(AAAAClass(source=randomNum.nextGaussian()))
        Thread.sleep(500)
      }

    }

    override def cancel(): Unit = {
      running = false
    }
  }
}
