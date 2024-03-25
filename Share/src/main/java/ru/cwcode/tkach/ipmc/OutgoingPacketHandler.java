package ru.cwcode.tkach.ipmc;

import java.util.HashMap;

public abstract class OutgoingPacketHandler<C, P extends Packet, S> {
  protected S source;
  HashMap<String, Class<? extends Packet>> registeredOutgoingPackets = new HashMap<>();
  
  public OutgoingPacketHandler(S source) {
    this.source = source;
  }
  
  public void register(String channel, Class<? extends Packet> packetClass) {
    registeredOutgoingPackets.put(channel, packetClass);
  }
  
  public void unregister(String channel) {
    registeredOutgoingPackets.remove(channel);
  }
  public abstract void send(P packet, C connection);
  public void onShutdown() {
    for (String channel : registeredOutgoingPackets.keySet()) {
      unregister(channel);
    }
  }
}
