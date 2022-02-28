package io.memoria.mkafka.adapter;

import io.memoria.reactive.core.stream.OMsg;
import io.memoria.reactive.core.stream.OStreamRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OStreamKafkaRepo implements OStreamRepo {

  @Override
  public Mono<String> create(String topic) {
    return null;
  }

  @Override
  public Mono<Integer> publish(String topic, OMsg oMsg) {
    return null;
  }

  @Override
  public Mono<Integer> size(String topic) {
    return null;
  }

  @Override
  public Flux<OMsg> subscribe(String topic, int skipped) {
    return null;
  }
}
