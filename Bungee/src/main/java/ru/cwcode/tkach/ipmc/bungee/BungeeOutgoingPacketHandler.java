package ru.cwcode.tkach.ipmc.bungee;

import net.md_5.bungee.api.connection.Server;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;

public class BungeeOutgoingPacketHandler extends OutgoingPacketHandler<Server, Packet, IPMC> {
  
  public BungeeOutgoingPacketHandler(IPMC source) {
    super(source);
  }
  
  @Override
  public void send(Packet packet, Server server) {
    server.sendData(packet.channel(), packet.asByteArray());
  }
}
