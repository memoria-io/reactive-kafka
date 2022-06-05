package io.memoria.reactive.kafka;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KafkaUtilsTest {
  @Test
  void topicSize() {
    var conf = Dataset.consumerConfigs();
    var size = KafkaUtils.topicSize("some_topic", 0, conf);
    Assertions.assertEquals(0, size);
  }
}
