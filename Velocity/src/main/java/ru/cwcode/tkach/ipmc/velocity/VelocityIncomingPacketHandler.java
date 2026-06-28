package ru.cwcode.tkach.ipmc.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.text.Component;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketOptions;
import ru.cwcode.tkach.ipmc.PacketUtils;

import java.util.logging.Logger;

public class VelocityIncomingPacketHandler extends IncomingPacketHandler<ServerConnection, Packet, IPMC> {
  private final String minecraftChannel;
  private final boolean clientChannel;
  private final int maxPacketBytes;
  
  public VelocityIncomingPacketHandler(IPMC plugin) {
    this(plugin, PacketUtils.INTERNAL_CHANNEL, false, PacketOptions.UNLIMITED_BYTES);
  }
  
  public VelocityIncomingPacketHandler(IPMC plugin, String minecraftChannel, boolean clientChannel, int maxPacketBytes) {
    super(plugin);
    this.minecraftChannel = minecraftChannel;
    this.clientChannel = clientChannel;
    this.maxPacketBytes = maxPacketBytes;
    
    MinecraftChannelIdentifier channelInst = MinecraftChannelIdentifier.from(minecraftChannel);
    plugin.server.getChannelRegistrar().register(channelInst);
    plugin.server.getEventManager().register(plugin, this);
  }
  
  @Subscribe
  protected void onPluginMessage(PluginMessageEvent event) {
    String channelId = event.getIdentifier().getId();
    if (!channelId.equalsIgnoreCase(minecraftChannel)) return;
    
    if (clientChannel) {
      event.setResult(PluginMessageEvent.ForwardResult.forward());
      return;
    }
    
    if (event.getSource() instanceof ServerConnection sc) {
      if (maxPacketBytes > PacketOptions.UNLIMITED_BYTES && event.getData().length > maxPacketBytes) return;
      
      String channel = PacketUtils.extractChannel(event.dataAsInputStream());
      if (channel == null) return;
      
      receive(sc, channel, event.getData());
      event.setResult(PluginMessageEvent.ForwardResult.handled());
    } else if (event.getSource() instanceof Player player) {
      event.setResult(PluginMessageEvent.ForwardResult.handled());
      Logger.getAnonymousLogger().severe("Player %s trying to hack the server using PMC".formatted(player.getUsername()));
      player.disconnect(Component.text("Hacking is not allowed on this server"));
    }
  }
}
