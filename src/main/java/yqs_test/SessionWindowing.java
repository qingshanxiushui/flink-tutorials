package yqs_test;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class SessionWindowing {
    public SessionWindowing() {
    }

    public static void main(String[] args) throws Exception {
        ParameterTool params = ParameterTool.fromArgs(args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(params);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);
        boolean fileOutput = params.has("output");
        final List<Tuple3<String, Long, Integer>> input = new ArrayList();
        input.add(new Tuple3("a", 1L, 1));
        input.add(new Tuple3("b", 1L, 1));
        input.add(new Tuple3("b", 3L, 1));
        input.add(new Tuple3("b", 5L, 1));
        input.add(new Tuple3("c", 6L, 1));
        input.add(new Tuple3("a", 10L, 1));
        input.add(new Tuple3("c", 11L, 1));
        DataStream<Tuple3<String, Long, Integer>> source = env.addSource(new SourceFunction<Tuple3<String, Long, Integer>>() {
            private static final long serialVersionUID = 1L;

            public void run(SourceContext<Tuple3<String, Long, Integer>> ctx) throws Exception {
                Iterator var2 = input.iterator();

                while(var2.hasNext()) {
                    Tuple3<String, Long, Integer> value = (Tuple3)var2.next();
                    ctx.collectWithTimestamp(value, (Long)value.f1);
                    ctx.emitWatermark(new Watermark((Long)value.f1 - 1L));
                }

                ctx.emitWatermark(new Watermark(9223372036854775807L));
            }

            public void cancel() {
            }
        });
        DataStream<Tuple3<String, Long, Integer>> aggregated = source.keyBy(new int[]{0}).window(EventTimeSessionWindows.withGap(Time.milliseconds(3L))).sum(2);
        if (fileOutput) {
            aggregated.writeAsText(params.get("output"));
        } else {
            System.out.println("Printing result to stdout. Use --output to specify output path.");
            aggregated.print();
        }

        env.execute();
    }
}
