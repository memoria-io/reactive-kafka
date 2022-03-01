package io.memoria.mkafka.adapter;

import io.memoria.reactive.core.stream.OStreamRepo;
import io.vavr.collection.Map;

public interface OStreamKafkaRepo extends OStreamRepo {
  static OStreamRepo create(Map<String, Object> producerConfig, Map<String, Object> consumerConfig) {
    return new DefaultOStreamKafkaRepo(producerConfig, consumerConfig);
  }
}
