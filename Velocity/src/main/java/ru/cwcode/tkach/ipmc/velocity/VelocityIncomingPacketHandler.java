package ru.cwcode.tkach.ipmc.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.text.Component;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketUtils;

import java.util.logging.Logger;

public class VelocityIncomingPacketHandler extends IncomingPacketHandler<ServerConnection, Packet, IPMC> {
  public VelocityIncomingPacketHandler(IPMC plugin) {
    super(plugin);
    
    MinecraftChannelIdentifier channelInst = MinecraftChannelIdentifier.from(PacketUtils.INTERNAL_CHANNEL);
    plugin.server.getChannelRegistrar().register(channelInst);
    plugin.server.getEventManager().register(plugin,this);
  }
  
  @Subscribe
  protected void onPluginMessage(PluginMessageEvent event) {
    if (event.getSource() instanceof ServerConnection sc) {
      String channel = PacketUtils.extractChannel(event.dataAsInputStream());
      if (channel == null) return;
      
      receive(sc, channel, event.getData());
      event.setResult(PluginMessageEvent.ForwardResult.handled());
    } else if (event.getSource() instanceof Player player && (event.getIdentifier().getId().startsWith("ipmc"))) {
      
      event.setResult(PluginMessageEvent.ForwardResult.handled());
      Logger.getAnonymousLogger().severe("Player %s trying to hack the server using PMC".formatted(player.getUsername()));
      player.disconnect(Component.text("Hacking is not allowed on this server"));
    }
  }
}
