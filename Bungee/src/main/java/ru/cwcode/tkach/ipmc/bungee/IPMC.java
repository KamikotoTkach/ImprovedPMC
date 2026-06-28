package ru.cwcode.tkach.ipmc.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketManager;
import ru.cwcode.tkach.ipmc.bungee.wrapper.ServerConnection;

public class IPMC extends Plugin {
  protected static PacketManager<ServerConnection, Packet, IPMC, IPMC, BungeeIncomingPacketHandler, BungeeOutgoingPacketHandler> packetManager;
  protected static PacketManager<ServerConnection, Packet, IPMC, IPMC, BungeeIncomingPacketHandler, BungeeOutgoingPacketHandler> clientPacketManager;
  
  public static PacketManager<ServerConnection, Packet, IPMC, IPMC, BungeeIncomingPacketHandler, BungeeOutgoingPacketHandler> packetManager() {
    return packetManager;
  }
  
  public static PacketManager<ServerConnection, Packet, IPMC, IPMC, BungeeIncomingPacketHandler, BungeeOutgoingPacketHandler> clientPacketManager() {
    return clientPacketManager;
  }
  
  @Override
  public void onEnable() {
    packetManager = new PacketManager<>(new BungeeIncomingPacketHandler(this),
                                        new BungeeOutgoingPacketHandler(this));
    clientPacketManager = new PacketManager<>(new BungeeIncomingPacketHandler(this,
                                                                              ru.cwcode.tkach.ipmc.PacketUtils.CLIENT_CHANNEL,
                                                                              true,
                                                                              ru.cwcode.tkach.ipmc.PacketUtils.DEFAULT_CLIENT_MAX_PACKET_BYTES),
                                               new BungeeOutgoingPacketHandler(this, ru.cwcode.tkach.ipmc.PacketUtils.CLIENT_CHANNEL));
  }
}
