package io.memoria.mkafka;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.Random;

@TestMethodOrder(OrderAnnotation.class)
class DefaultKafkaStreamTest {
  private static final Random random = new Random();
  private static final int MSG_COUNT = 1000;
  private static final String TOPIC = "SomeTopic" + random.nextInt(1000);
  private static final int PARTITION = 0;
  private static final DefaultKafkaStream repo;

  static {
    repo = new DefaultKafkaStream(TestUtils.producerConfigs(), TestUtils.consumerConfigs(), () -> 1L);
  }

  @Test
  @Order(0)
  void sizeBefore() {
    var size = repo.size(TOPIC, PARTITION);
    StepVerifier.create(size).expectNext(0L).verifyComplete();
  }

  @Test
  @Order(1)
  void publish() {
    // Given
    var msgs = Flux.range(0, MSG_COUNT).map(i -> new Msg(TOPIC, PARTITION, Id.of(i), "hello" + i));
    // When
    var pub = repo.publish(msgs);
    var size = repo.size(TOPIC, PARTITION);
    // Then
    StepVerifier.create(pub).expectNextCount(MSG_COUNT).verifyComplete();
    StepVerifier.create(size).expectNext((long) MSG_COUNT).verifyComplete();
  }

  @Test
  @Order(2)
  void subscribe() {
    // Given previous publish ran successfully
    // When
    var sub = repo.subscribe(TOPIC, PARTITION, 0).take(MSG_COUNT);
    // Given
    StepVerifier.create(sub).expectNextCount(MSG_COUNT).verifyComplete();
  }

  @Test
  @Order(3)
  void sizeAfter() {
    var size = repo.size(TOPIC, PARTITION);
    StepVerifier.create(size).expectNext(1000L).verifyComplete();
  }
}
