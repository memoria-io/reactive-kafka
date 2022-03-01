package io.memoria.mkafka.adapter;

import io.memoria.reactive.core.stream.OMsg;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Random;

class DefaultOStreamKafkaRepoTest {
  private static final Random random = new Random();
  private static final int MSG_COUNT = 10;
  private static final String topic = "SomeTopic" + random.nextInt(1000);
  private static final int partition = 0;
  private static final DefaultOStreamKafkaRepo repo;

  static {
    repo = new DefaultOStreamKafkaRepo(TestUtils.producerConfigs(), TestUtils.consumerConfigs());
  }

  @Test
  void publish() {
    // Given
    var msgs = Flux.range(0, MSG_COUNT).map(i -> new OMsg(i, "hello" + i));
    // When
    var pub = repo.publish(topic, partition, msgs);
    var size = repo.size(topic, partition);
    // Then
    StepVerifier.create(pub).expectNextCount(MSG_COUNT).verifyComplete();
    StepVerifier.create(size).expectNext((long) MSG_COUNT).verifyComplete();
  }
}
