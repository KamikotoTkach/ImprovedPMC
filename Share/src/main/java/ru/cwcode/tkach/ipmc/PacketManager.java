package ru.cwcode.tkach.ipmc;

import java.util.function.BiConsumer;

public class PacketManager<C, S> {
  IncomingPacketHandler<C, Packet, S> incoming;
  OutgoingPacketHandler<C, Packet, S> outgoing;
  
  public PacketManager(IncomingPacketHandler<C, Packet, S> incoming,
                       OutgoingPacketHandler<C, Packet, S> outgoing) {
    this.incoming = incoming;
    this.outgoing = outgoing;
  }
  
  public <X extends Packet> void registerIncomingPacket(Class<X> packetClass, BiConsumer<C, X> onReceive) {
    incoming.register(PacketUtils.extractChannel(packetClass), (Class<Packet>) packetClass, (BiConsumer<C, Packet>) onReceive);
  }
  
  public void registerOutgoingPacket(Class<? extends Packet> packetClass) {
    outgoing.register(PacketUtils.extractChannel(packetClass), packetClass);
  }
  
  public void onShutdown() {
    incoming.onShutdown();
    outgoing.onShutdown();
  }
  
  public void send(Packet packet, C connection) {
    outgoing.send(packet, connection);
  }
}
