package io.memoria.mkafka.adapter;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class TestUtils {
  private TestUtils() {}

  public static Map<String, Object> consumerConfigs() {
    return HashMap.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                      "localhost:9092",
                      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                      LongDeserializer.class,
                      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                      StringDeserializer.class,
                      ConsumerConfig.GROUP_ID_CONFIG,
                      "some_group_id");
  }

  public static Map<String, Object> producerConfigs() {
    return HashMap.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                      "localhost:9092",
                      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                      LongSerializer.class,
                      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                      StringSerializer.class);
  }
}
