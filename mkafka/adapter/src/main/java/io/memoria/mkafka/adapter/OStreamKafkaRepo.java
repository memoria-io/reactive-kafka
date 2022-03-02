package io.memoria.mkafka.adapter;

import io.memoria.reactive.core.stream.OStreamRepo;
import io.vavr.collection.Map;

import java.util.function.Supplier;

public interface OStreamKafkaRepo extends OStreamRepo {
  static OStreamRepo create(Map<String, Object> producerConfig,
                            Map<String, Object> consumerConfig,
                            Supplier<Long> timeSupplier) {
    return new DefaultOStreamKafkaRepo(producerConfig, consumerConfig, timeSupplier);
  }

  static OStreamRepo create(Map<String, Object> producerConfig,
                            Map<String, Object> consumerConfig,
                            int maxInFlight,
                            Supplier<Long> timeSupplier) {
    return new DefaultOStreamKafkaRepo(producerConfig, consumerConfig, maxInFlight, timeSupplier);
  }
}
