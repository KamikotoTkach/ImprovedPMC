package ru.cwcode.tkach.ipmc.bungee;

import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Plugin;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketManager;

public class IPMC extends Plugin {
  protected static PacketManager<Server, Packet, IPMC, IPMC, BungeeIncomingPacketHandler, BungeeOutgoingPacketHandler> packetManager;
  
  public static PacketManager<Server, Packet, IPMC, IPMC, BungeeIncomingPacketHandler, BungeeOutgoingPacketHandler> packetManager() {
    return packetManager;
  }
  
  @Override
  public void onEnable() {
    packetManager = new PacketManager<>(new BungeeIncomingPacketHandler(this),
                                        new BungeeOutgoingPacketHandler(this));
  }
}
