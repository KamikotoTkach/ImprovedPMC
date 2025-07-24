package ru.cwcode.tkach.ipmc.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.bungee.wrapper.ServerConnection;

import java.util.function.BiConsumer;

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
    if (!(event.getSender() instanceof Server)) return;
    if (!(event.getReceiver() instanceof ProxiedPlayer)) return;
    
    Server server = (Server) event.getSender();
    ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
    
    String channel = event.getTag();
    ServerConnection serverConnection = new ServerConnection(server, player);
    
    receive(serverConnection, channel, event.getData());
  }
}
