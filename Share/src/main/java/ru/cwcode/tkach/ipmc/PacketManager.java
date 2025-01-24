package ru.cwcode.tkach.ipmc;

import java.util.function.BiConsumer;

public class PacketManager<C, P extends Packet, IS, OS, I extends IncomingPacketHandler<C, P, IS>, O extends OutgoingPacketHandler<C, P, OS>> {
  protected I incoming;
  protected O outgoing;
  
  public PacketManager(I incoming, O outgoing) {
    this.incoming = incoming;
    this.outgoing = outgoing;
  }
  
  public <X extends Packet> void registerIncomingPacket(Class<X> packetClass, BiConsumer<C, X> onReceive) {
    incoming.register(PacketUtils.extractChannel(packetClass), (Class<P>) packetClass, (BiConsumer<C, P>) onReceive);
  }
  
  public void registerOutgoingPacket(Class<? extends Packet> packetClass) {
    outgoing.register(PacketUtils.extractChannel(packetClass), packetClass);
  }
  
  public void onShutdown() {
    incoming.onShutdown();
    outgoing.onShutdown();
  }
  
  public void send(P packet, C connection) {
    outgoing.register(packet.channel(),packet.getClass());
    outgoing.send(packet, connection);
  }
}
