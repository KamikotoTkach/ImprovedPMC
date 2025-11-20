package ru.cwcode.tkach.ipmc.bungee;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketUtils;
import ru.cwcode.tkach.ipmc.bungee.wrapper.ServerConnection;

import java.util.function.BiConsumer;
import java.util.logging.Logger;

public class BungeeIncomingPacketHandler extends IncomingPacketHandler<ServerConnection, Packet, IPMC> implements Listener {
  public BungeeIncomingPacketHandler(IPMC plugin) {
    super(plugin);
    
    plugin.getProxy().getPluginManager().registerListener(plugin, this);
  }
  
  @Override
  public void register(String channel, Class<Packet> packetClass, BiConsumer<ServerConnection, Packet> onReceive) {
    if (isRegistered(channel)) return;
    
    super.register(channel, packetClass, onReceive);
    
    plugin.getProxy().registerChannel(channel);
  }
  
  @EventHandler
  public void onPluginMessage(PluginMessageEvent event) {
    if (event.getSender() instanceof ProxiedPlayer player) {
      if (event.getTag().startsWith("ipmc")
          || PacketUtils.isJavaSerializationPacket(event.getData())) {
        Logger.getAnonymousLogger().severe("Player %s trying to hack the server using PMC".formatted(player.getName()));
        player.disconnect(TextComponent.fromLegacy("Hacking is not allowed on this server"));
        event.setCancelled(true);
        return;
      }
    }
    
    if (!(event.getSender() instanceof Server server)) return;
    if (!(event.getReceiver() instanceof ProxiedPlayer player)) return;
    
    String channel = event.getTag();
    
    ServerConnection serverConnection = new ServerConnection(server, player);
    
    receive(serverConnection, channel, event.getData());
    event.setCancelled(true);
  }
}
