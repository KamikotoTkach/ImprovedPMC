package ru.cwcode.tkach.ipmc;

import java.util.function.BiConsumer;

public class IncomingPacketWrapper<P, T extends Packet> {
  Class<T> packetClass;
  BiConsumer<P, T> onReceive;
  
  public IncomingPacketWrapper(Class<T> packetClass, BiConsumer<P, T> onReceive) {
    this.packetClass = packetClass;
    this.onReceive = onReceive;
  }
  
  public Class<? extends Packet> packetClass() {
    return packetClass;
  }
  
  public void onReceive(P player, T packet) {
    onReceive.accept(player, packet);
  }
}
