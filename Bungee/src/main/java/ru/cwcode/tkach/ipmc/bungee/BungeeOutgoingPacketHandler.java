package ru.cwcode.tkach.ipmc.bungee;

import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketUtils;
import ru.cwcode.tkach.ipmc.bungee.wrapper.ServerConnection;

public class BungeeOutgoingPacketHandler extends OutgoingPacketHandler<ServerConnection, Packet, IPMC> {
  private final String minecraftChannel;
  
  public BungeeOutgoingPacketHandler(IPMC source) {
    this(source, PacketUtils.INTERNAL_CHANNEL);
  }
  
  public BungeeOutgoingPacketHandler(IPMC source, String minecraftChannel) {
    super(source);
    this.minecraftChannel = minecraftChannel;
  }
  
  @Override
  public void send(Packet packet, ServerConnection serverConnection) {
    serverConnection.getServer().sendData(minecraftChannel, packet.write());
  }
}
