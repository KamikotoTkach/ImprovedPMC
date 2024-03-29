package ru.cwcode.tkach.ipmc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class IncomingPacketWrapper<P, T extends Packet> {
  protected Class<T> packetClass;
  protected List<BiConsumer<P, T>> onReceive;
  
  public IncomingPacketWrapper(Class<T> packetClass) {
    this.packetClass = packetClass;
    this.onReceive = new ArrayList<>();
  }
  
  public void addConsumer(BiConsumer<P, T> onReceive) {
    this.onReceive.add(onReceive);
  }
  
  public Class<? extends Packet> packetClass() {
    return packetClass;
  }
  
  public void onReceive(P player, T packet) {
    for (BiConsumer<P, T> consumer : onReceive) {
      consumer.accept(player, packet);
    }
  }
}
