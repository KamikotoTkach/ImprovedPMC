package ru.cwcode.tkach.ipmc.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketManager;

@Plugin(
  id = "ipmc",
  name = "IPMC",
  version = "1.5.0"
)
public class IPMC {
  protected static PacketManager<ServerConnection, Packet, IPMC, IPMC, VelocityIncomingPacketHandler, VelocityOutgoingPacketHandler> packetManager;
  @Inject
  ProxyServer server;
  
  public static PacketManager<ServerConnection, Packet, IPMC, IPMC, VelocityIncomingPacketHandler, VelocityOutgoingPacketHandler> packetManager() {
    return packetManager;
  }
  
  @Subscribe
  public void onProxyInitialize(ProxyInitializeEvent event) {
    packetManager = new PacketManager<>(new VelocityIncomingPacketHandler(this),
                                        new VelocityOutgoingPacketHandler(this));
  }
}
