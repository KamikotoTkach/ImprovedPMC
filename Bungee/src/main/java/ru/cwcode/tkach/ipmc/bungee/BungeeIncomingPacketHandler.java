package ru.cwcode.tkach.ipmc.bungee;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketOptions;
import ru.cwcode.tkach.ipmc.PacketUtils;
import ru.cwcode.tkach.ipmc.bungee.wrapper.ServerConnection;

import java.util.logging.Logger;

public class BungeeIncomingPacketHandler extends IncomingPacketHandler<ServerConnection, Packet, IPMC> implements Listener {
  private final String minecraftChannel;
  private final boolean clientChannel;
  private final int maxPacketBytes;
  
  public BungeeIncomingPacketHandler(IPMC plugin) {
    this(plugin, PacketUtils.INTERNAL_CHANNEL, false, PacketOptions.UNLIMITED_BYTES);
  }
  
  public BungeeIncomingPacketHandler(IPMC plugin, String minecraftChannel, boolean clientChannel, int maxPacketBytes) {
    super(plugin);
    this.minecraftChannel = minecraftChannel;
    this.clientChannel = clientChannel;
    this.maxPacketBytes = maxPacketBytes;
    
    plugin.getProxy().registerChannel(minecraftChannel);
    plugin.getProxy().getPluginManager().registerListener(plugin, this);
  }
  
  @EventHandler
  public void onPluginMessage(PluginMessageEvent event) {
    String channelId = event.getTag();
    if (!channelId.equalsIgnoreCase(minecraftChannel)) return;
    
    if (clientChannel) return;
    
    if (event.getSender() instanceof ProxiedPlayer player) {
      Logger.getAnonymousLogger().severe("Player %s trying to hack the server using PMC".formatted(player.getName()));
      player.disconnect(TextComponent.fromLegacy("Hacking is not allowed on this server"));
      event.setCancelled(true);
      return;
    }
    
    if (!(event.getSender() instanceof Server server)
        || !(event.getReceiver() instanceof ProxiedPlayer player)) {
      event.setCancelled(true);
      return;
    }
    
    if (maxPacketBytes > PacketOptions.UNLIMITED_BYTES && event.getData().length > maxPacketBytes) {
      event.setCancelled(true);
      return;
    }
    
    String channel = PacketUtils.extractChannel(event.getData());
    if (channel == null) return;
    
    ServerConnection serverConnection = new ServerConnection(server, player);
    
    receive(serverConnection, channel, event.getData());
    event.setCancelled(true);
  }
}
