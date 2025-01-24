package ru.cwcode.tkach.ipmc.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketUtils;

public class KafkaOutgoingPacketHandler extends OutgoingPacketHandler<String, Packet, KafkaProvider> {
  public KafkaOutgoingPacketHandler(KafkaProvider kafkaProvider) {
    super(kafkaProvider);
  }
  
  @Override
  public void send(Packet packet, String connection) {
    String channel = PacketUtils.extractChannel(packet.getClass());
    if (!isRegistered(channel) || channel == null) {
      throw new IllegalStateException("Channel not registered: " + channel);
    }
    
    try {
      ProducerRecord<String, byte[]> record = new ProducerRecord<>(channel, packet.asByteArray());
      source.getProducer().send(record);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
