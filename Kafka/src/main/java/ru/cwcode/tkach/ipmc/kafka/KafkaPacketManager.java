package ru.cwcode.tkach.ipmc.kafka;

import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketManager;

public class KafkaPacketManager extends PacketManager<String, Packet, KafkaProvider, KafkaProvider, KafkaIncomingPacketHandler, KafkaOutgoingPacketHandler> {
  
  public KafkaPacketManager(KafkaProvider kafkaProvider) {
    super(new KafkaIncomingPacketHandler(kafkaProvider), new KafkaOutgoingPacketHandler(kafkaProvider));
  }
  
  public void startListening() {
    incoming.startListening();
  }
}
