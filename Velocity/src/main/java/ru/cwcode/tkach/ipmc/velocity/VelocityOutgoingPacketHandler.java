package ru.cwcode.tkach.ipmc.velocity;

import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;

public class VelocityOutgoingPacketHandler extends OutgoingPacketHandler<ServerConnection, Packet, IPMC> {
  
  public VelocityOutgoingPacketHandler(IPMC source) {
    super(source);
  }
  
  @Override
  public void register(String channel, Class<? extends Packet> packetClass) {
    super.register(channel, packetClass);
  }
  
  @Override
  public void unregister(String channel) {
    super.unregister(channel);
  }
  
  @Override
  public void send(Packet packet, ServerConnection connection) {
    connection.sendPluginMessage(MinecraftChannelIdentifier.from(packet.channel()), packet.asByteArray());
  }
}
