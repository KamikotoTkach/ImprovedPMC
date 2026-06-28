package ru.cwcode.tkach.ipmc.velocity;

import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketUtils;

public class VelocityOutgoingPacketHandler extends OutgoingPacketHandler<ServerConnection, Packet, IPMC> {
  private final String minecraftChannel;
  private final MinecraftChannelIdentifier channelIdentifier;
  
  public VelocityOutgoingPacketHandler(IPMC source) {
    this(source, PacketUtils.INTERNAL_CHANNEL);
  }
  
  public VelocityOutgoingPacketHandler(IPMC source, String minecraftChannel) {
    super(source);
    this.minecraftChannel = minecraftChannel;
    this.channelIdentifier = MinecraftChannelIdentifier.from(minecraftChannel);
  }
  
  @Override
  public void send(Packet packet, ServerConnection connection) {
    connection.sendPluginMessage(channelIdentifier, packet.write());
  }
}
