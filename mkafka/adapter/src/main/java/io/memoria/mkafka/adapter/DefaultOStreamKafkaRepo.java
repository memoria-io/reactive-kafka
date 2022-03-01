package io.memoria.mkafka.adapter;

import io.memoria.reactive.core.stream.OMsg;
import io.vavr.collection.Map;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;
import java.util.function.Supplier;

class DefaultOStreamKafkaRepo implements OStreamKafkaRepo {
  public static final ZoneOffset utc = ZoneOffset.UTC;
  private final Map<String, Object> producerConfig;
  private final Map<String, Object> consumerConfig;
  private final int maxInFlight;

  private final KafkaSender<Long, String> sender;
  private final KafkaReceiver<Long, String> receiver;
  private final Supplier<LocalDateTime> timeSupplier;

  DefaultOStreamKafkaRepo(Map<String, Object> producerConfig, Map<String, Object> consumerConfig) {
    this(producerConfig, consumerConfig, 1024, LocalDateTime::now);
  }

  DefaultOStreamKafkaRepo(Map<String, Object> producerConfig,
                          Map<String, Object> consumerConfig,
                          int maxInFlight,
                          Supplier<LocalDateTime> timeSupplier) {
    this.producerConfig = producerConfig;
    this.consumerConfig = consumerConfig;
    this.maxInFlight = maxInFlight;

    this.sender = RKafkaUtils.createSender(producerConfig, maxInFlight);
    this.receiver = RKafkaUtils.createReceiver(consumerConfig);
    this.timeSupplier = timeSupplier;
  }

  @Override
  public Mono<Long> publish(String topic, int partition, OMsg oMsg) {
    var senderRec = toRecord(topic, partition, oMsg);
    return sender.send(Mono.just(senderRec)).next().map(SenderResult::correlationMetadata);
  }

  @Override
  public Flux<Long> publish(String topic, int partition, Flux<OMsg> msgs) {
    var records = msgs.map(msg -> toRecord(topic, partition, msg));
    return sender.send(records).map(SenderResult::correlationMetadata);
  }

  @Override
  public Mono<Long> size(String topic, int partition) {
    return Mono.fromCallable(() -> RKafkaUtils.topicSize(topic, partition, consumerConfig));
  }

  @Override
  public Flux<OMsg> subscribe(String topic, int partition, long skipped) {
    return this.receiver.receiveAutoAck().concatMap(Function.identity()).map(RKafkaUtils::toOMsg);
  }

  private SenderRecord<Long, String, Long> toRecord(String topic, int partition, OMsg oMsg) {
    var timestamp = timeSupplier.get().toEpochSecond(utc);
    return SenderRecord.create(topic, partition, timestamp, oMsg.sKey(), oMsg.value(), oMsg.sKey());
  }
}
