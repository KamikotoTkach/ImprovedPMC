package ru.cwcode.tkach.ipmc.velocity;

import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketUtils;

public class VelocityOutgoingPacketHandler extends OutgoingPacketHandler<ServerConnection, Packet, IPMC> {
  
  public VelocityOutgoingPacketHandler(IPMC source) {
    super(source);
  }
  
  @Override
  public void send(Packet packet, ServerConnection connection) {
    connection.sendPluginMessage(MinecraftChannelIdentifier.from(PacketUtils.INTERNAL_CHANNEL), packet.write());
  }
}
