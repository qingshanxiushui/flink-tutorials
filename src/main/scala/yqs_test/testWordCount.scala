package yqs_test

import org.apache.flink.api.scala._
object testWordCount {
  def main(args: Array[String]): Unit = {

    // set up the execution environment
    val env = ExecutionEnvironment.getExecutionEnvironment

    // get input data
    val text = env.fromElements(
      "hello world!",
      "hello world!",
      "hello world!")

    val counts = text.flatMap { _.toLowerCase.split(" ") }
      .map { (_, 1) }
      .groupBy(0)
      .sum(1)

    // execute and print result
    counts.print()

  }
}

