package ru.cwcode.tkach.ipmc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class OutgoingPacketHandler<C, P extends Packet, S> {
  protected S source;
  protected ConcurrentMap<String, Class<? extends Packet>> registeredOutgoingPackets = new ConcurrentHashMap<>();
  
  public OutgoingPacketHandler(S source) {
    this.source = source;
  }
  
  public void register(String channel, Class<? extends Packet> packetClass) {
    registeredOutgoingPackets.putIfAbsent(channel, packetClass);
  }
  
  public boolean isRegistered(String channel) {
    return registeredOutgoingPackets.containsKey(channel);
  }
  
  public void unregister(String channel) {
    registeredOutgoingPackets.remove(channel);
  }
  
  public abstract void send(P packet, C connection);
  
  public void onShutdown() {
    registeredOutgoingPackets.clear();
  }
}
