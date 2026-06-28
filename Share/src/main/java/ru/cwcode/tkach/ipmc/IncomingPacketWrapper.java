package ru.cwcode.tkach.ipmc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public class IncomingPacketWrapper<P, T extends Packet> {
  protected Class<T> packetClass;
  protected PacketOptions options;
  protected List<BiConsumer<P, T>> onReceive;
  
  public IncomingPacketWrapper(Class<T> packetClass) {
    this(packetClass, PacketOptions.unlimited());
  }
  
  public IncomingPacketWrapper(Class<T> packetClass, PacketOptions options) {
    this.packetClass = packetClass;
    this.options = options;
    this.onReceive = new CopyOnWriteArrayList<>();
  }
  
  public void addConsumer(BiConsumer<P, T> onReceive) {
    this.onReceive.add(onReceive);
  }
  
  public Class<? extends Packet> packetClass() {
    return packetClass;
  }
  
  public PacketOptions options() {
    return options;
  }
  
  public void onReceive(P player, T packet) {
    for (BiConsumer<P, T> consumer : onReceive) {
      try {
        consumer.accept(player, packet);
      } catch (Exception e) {
        Logger.getLogger("IPMC").warning("Cannot process packet %s for player %s: %s (consumer %s)"
                                            .formatted(packet.channel(),
                                                       player,
                                                       e.getMessage(),
                                                       consumer.getClass().getName()));
      }
    }
  }
}
