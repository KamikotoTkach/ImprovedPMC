package ru.cwcode.tkach.ipmc.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;

import java.util.function.BiConsumer;

public class BungeeIncomingPacketHandler extends IncomingPacketHandler<ProxiedPlayer, Packet, IPMC> implements Listener {
  public BungeeIncomingPacketHandler(IPMC plugin) {
    super(plugin);
    
    plugin.getProxy().getPluginManager().registerListener(plugin, this);
  }
  
  @Override
  public void register(String channel, Class<Packet> packetClass, BiConsumer<ProxiedPlayer, Packet> onReceive) {
    if (isRegistered(channel)) return;
    
    super.register(channel, packetClass, onReceive);
    
    plugin.getProxy().registerChannel(channel);
  }
  
  @EventHandler
  protected void onPluginMessage(PluginMessageEvent event) {
    if (event.getSender() instanceof ProxiedPlayer proxiedPlayer) {
      String channel = event.getTag();
      receive(proxiedPlayer, channel, event.getData());
    }
  }
}
