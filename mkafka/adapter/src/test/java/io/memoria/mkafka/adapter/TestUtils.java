package io.memoria.mkafka.adapter;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.kafka.sender.SenderRecord;

public class TestUtils {
  private TestUtils() {}

  public static SenderRecord<Long, String, Long> createMsg(String topic, int partition, long timestamp, long i) {
    return SenderRecord.create(topic, partition, timestamp, i, "Message_" + i, i);
  }

  public static Map<String, Object> producerConfigs() {
    return HashMap.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                      "localhost:9092",
                      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                      LongSerializer.class,
                      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                      StringSerializer.class);
  }

  public static Map<String, Object> consumerConfigs() {
    return HashMap.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                      "localhost:9092",
                      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                      LongDeserializer.class,
                      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                      StringDeserializer.class);
  }
}
