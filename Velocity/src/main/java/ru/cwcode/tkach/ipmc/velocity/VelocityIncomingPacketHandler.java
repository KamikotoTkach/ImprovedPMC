package ru.cwcode.tkach.ipmc.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;

import java.util.function.BiConsumer;

public class VelocityIncomingPacketHandler extends IncomingPacketHandler<ServerConnection, Packet, IPMC> {
  public VelocityIncomingPacketHandler(IPMC plugin) {
    super(plugin);
  }
  
  @Override
  public void register(String channel, Class<Packet> packetClass, BiConsumer<ServerConnection, Packet> onReceive) {
    super.register(channel, packetClass, onReceive);
    MinecraftChannelIdentifier channelInst = MinecraftChannelIdentifier.from(channel);
    plugin.registerIdentifier(channel, channelInst);
    plugin.server.getChannelRegistrar().register(channelInst);
  }
  
  @Subscribe
  protected void onPluginMessage(PluginMessageEvent event) {
    if (event.getSource() instanceof ServerConnection) {
      String channel = plugin.identifiersRev.get(event.getIdentifier());
      if (channel == null) return;
      
      receive((ServerConnection) event.getSource(), channel, event.getData());
    }
  }
}
