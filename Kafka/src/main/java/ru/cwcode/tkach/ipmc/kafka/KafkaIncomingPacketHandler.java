package ru.cwcode.tkach.ipmc.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class KafkaIncomingPacketHandler extends IncomingPacketHandler<String, Packet, KafkaProvider> {
  
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  volatile boolean isListening = false;
  
  public KafkaIncomingPacketHandler(KafkaProvider kafkaProvider) {
    super(kafkaProvider);
  }
  
  @Override
  public void register(String channel, Class<Packet> packetClass, BiConsumer<String, Packet> onReceive) {
    super.register(channel, packetClass, onReceive);
    
    if (isListening) {
      plugin.getConsumer().subscribe(registeredIncomingPackets.keySet());
    }
  }
  
  public void startListening() {
    executor.submit(() -> {
      try {
        plugin.getConsumer().subscribe(registeredIncomingPackets.keySet());
        isListening = true;
        while (!Thread.currentThread().isInterrupted()) {
          ConsumerRecords<String, byte[]> records = plugin.getConsumer().poll(Duration.ofMillis(100));
          for (ConsumerRecord<String, byte[]> record : records) {
            receive(record.topic(), record.topic(), record.value());
          }
        }
      } catch (WakeupException ignored) {
        // Ignored for shutdown
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
  
  @Override
  public void onShutdown() {
    super.onShutdown();
    
    plugin.getConsumer().wakeup();
    executor.shutdownNow();
  }
  
}
