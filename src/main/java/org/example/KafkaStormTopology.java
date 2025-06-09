package org.example;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.FirstPollOffsetStrategy;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.topology.BasicOutputCollector;

public class KafkaStormTopology {

    // Bolt xử lý dữ liệu nhận được từ Kafka
    public static class ProcessingBolt extends BaseBasicBolt {
        @Override
        public void execute(Tuple input, BasicOutputCollector collector) {
            System.out.println("Received message: " + input.getValueByField("value"));
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            // Không có emit thêm output nên để trống
        }
    }

    public static void main(String[] args) throws Exception {
        // Kết nối Kafka local (host machine)
        String bootstrapServers = "localhost:9092";
        String topic = "test-topic";

        // Cấu hình Spout đọc từ Kafka
        KafkaSpoutConfig<String, String> spoutConfig = KafkaSpoutConfig.builder(bootstrapServers, topic)
                .setGroupId("storm-group")
                .setFirstPollOffsetStrategy(FirstPollOffsetStrategy.LATEST)
                .setProp("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
                .setProp("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
                .build();

        // Xây dựng topology
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kafka-spout", new KafkaSpout<>(spoutConfig), 1);
        builder.setBolt("processing-bolt", new ProcessingBolt(), 1).shuffleGrouping("kafka-spout");

        Config config = new Config();
        config.setNumWorkers(1);

        // Chạy LocalCluster cho test
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("kafka-storm-topology", config, builder.createTopology());

        // Chạy thử 1 phút
        Thread.sleep(60000);
        cluster.shutdown();
    }
}

