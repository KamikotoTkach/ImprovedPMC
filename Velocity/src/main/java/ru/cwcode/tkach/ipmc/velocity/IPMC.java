package ru.cwcode.tkach.ipmc.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketManager;
import ru.cwcode.tkach.ipmc.PacketUtils;

@Plugin(
   id = "ipmc",
   name = "IPMC",
   version = "1.5.1"
)
public class IPMC {
  protected static PacketManager<ServerConnection, Packet, IPMC, IPMC, VelocityIncomingPacketHandler, VelocityOutgoingPacketHandler> packetManager;
  protected static PacketManager<ServerConnection, Packet, IPMC, IPMC, VelocityIncomingPacketHandler, VelocityOutgoingPacketHandler> clientPacketManager;
  @Inject
  ProxyServer server;
  
  public static PacketManager<ServerConnection, Packet, IPMC, IPMC, VelocityIncomingPacketHandler, VelocityOutgoingPacketHandler> packetManager() {
    return packetManager;
  }
  
  public static PacketManager<ServerConnection, Packet, IPMC, IPMC, VelocityIncomingPacketHandler, VelocityOutgoingPacketHandler> clientPacketManager() {
    return clientPacketManager;
  }
  
  @Subscribe
  public void onProxyInitialize(ProxyInitializeEvent event) {
    packetManager = new PacketManager<>(new VelocityIncomingPacketHandler(this),
                                        new VelocityOutgoingPacketHandler(this));
    
    clientPacketManager = new PacketManager<>(new VelocityIncomingPacketHandler(this,
                                                                                PacketUtils.CLIENT_CHANNEL,
                                                                                true,
                                                                                PacketUtils.DEFAULT_CLIENT_MAX_PACKET_BYTES),
                                              new VelocityOutgoingPacketHandler(this, PacketUtils.CLIENT_CHANNEL));
  }
}
