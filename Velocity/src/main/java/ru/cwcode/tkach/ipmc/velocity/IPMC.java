package ru.cwcode.tkach.ipmc.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import ru.cwcode.tkach.ipmc.PacketManager;

import java.util.HashMap;

@Plugin(
   id = "ipmc",
   name = "IPMC",
   version = "1.0"
)
public class IPMC {
  protected static PacketManager<ServerConnection, IPMC> packetManager;
  HashMap<String, ChannelIdentifier> identifiers = new HashMap<>();
  HashMap<ChannelIdentifier, String> identifiersRev = new HashMap<>();
  @Inject
  ProxyServer server;
  
  public static PacketManager<ServerConnection, IPMC> packetManager() {
    return packetManager;
  }
  
  @Subscribe
  public void onProxyInitialize(ProxyInitializeEvent event) {
    packetManager = new PacketManager<>(new VelocityIncomingPacketHandler(this),
                                        new VelocityOutgoingPacketHandler(this));
  }
  
  public void registerIdentifier(String channel, MinecraftChannelIdentifier channelInst) {
    identifiersRev.put(channelInst, channel);
    identifiers.put(channel, channelInst);
  }
}
