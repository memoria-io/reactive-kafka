package io.memoria.mkafka;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.vavr.collection.Map;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.singleton;

class DefaultOStreamKafkaRepo implements OStreamKafkaRepo {
  public static final int DEFAULT_MAX_IN_FLIGHT = 1024;
  public final Map<String, Object> producerConfig;
  public final Map<String, Object> consumerConfig;
  public final int maxInFlight;
  private final Supplier<Long> timeSupplier;

  DefaultOStreamKafkaRepo(Map<String, Object> producerConfig,
                          Map<String, Object> consumerConfig,
                          Supplier<Long> timeSupplier) {
    this(producerConfig, consumerConfig, DEFAULT_MAX_IN_FLIGHT, timeSupplier);
  }

  DefaultOStreamKafkaRepo(Map<String, Object> producerConfig,
                          Map<String, Object> consumerConfig,
                          int maxInFlight,
                          Supplier<Long> timeSupplier) {
    this.producerConfig = producerConfig;
    this.consumerConfig = consumerConfig;
    this.maxInFlight = maxInFlight;
    this.timeSupplier = timeSupplier;
  }

  @Override
  public Mono<Id> publish(String topic, int partition, Msg oMsg) {
    var senderRec = toRecord(topic, partition, oMsg);
    return createSender().send(Mono.just(senderRec)).next().map(SenderResult::correlationMetadata);
  }

  @Override
  public Flux<Id> publish(String topic, int partition, Flux<Msg> msgs) {
    var records = msgs.map(msg -> toRecord(topic, partition, msg));
    return createSender().send(records).map(SenderResult::correlationMetadata);
  }

  @Override
  public Mono<Long> size(String topic, int partition) {
    return Mono.fromCallable(() -> RKafkaUtils.topicSize(topic, partition, consumerConfig));
  }

  @Override
  public Flux<Msg> subscribe(String topic, int partition, long offset) {
    var tp = new TopicPartition(topic, partition);
    var receiverOptions = ReceiverOptions.<String, String>create(consumerConfig.toJavaMap())
                                         .addAssignListener(partitions -> partitions.forEach(p -> p.seek(offset)))
                                         .assignment(singleton(tp));
    var receiver = KafkaReceiver.create(receiverOptions);
    return receiver.receiveAutoAck().concatMap(Function.identity()).map(RKafkaUtils::toMsg);
  }

  private KafkaSender<String, String> createSender() {
    var senderOptions = SenderOptions.<String, String>create(producerConfig.toJavaMap()).maxInFlight(maxInFlight);
    return KafkaSender.create(senderOptions);
  }

  private SenderRecord<String, String, Id> toRecord(String topic, int partition, Msg oMsg) {
    return SenderRecord.create(topic, partition, timeSupplier.get(), oMsg.id().value(), oMsg.value(), oMsg.id());
  }
}