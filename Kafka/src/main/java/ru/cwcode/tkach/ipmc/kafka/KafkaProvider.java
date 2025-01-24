package ru.cwcode.tkach.ipmc.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

public interface KafkaProvider {
  Consumer<String, byte[]> getConsumer();
  Producer<String, byte[]> getProducer();
}
