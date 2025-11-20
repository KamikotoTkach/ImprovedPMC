package ru.cwcode.tkach.ipmc.bungee;

import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.bungee.wrapper.ServerConnection;

public class BungeeOutgoingPacketHandler extends OutgoingPacketHandler<ServerConnection, Packet, IPMC> {
  
  public BungeeOutgoingPacketHandler(IPMC source) {
    super(source);
  }
  
  @Override
  public void send(Packet packet, ServerConnection serverConnection) {
    serverConnection.getServer().sendData(packet.channel(), packet.asByteArray());
  }
}
