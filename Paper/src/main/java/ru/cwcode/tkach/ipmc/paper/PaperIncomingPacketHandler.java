package ru.cwcode.tkach.ipmc.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;

import java.util.function.BiConsumer;

public class PaperIncomingPacketHandler extends IncomingPacketHandler<Player, Packet, JavaPlugin> {
  public PaperIncomingPacketHandler(JavaPlugin plugin) {
    super(plugin);
  }
  
  @Override
  public void register(String channel, Class<Packet> packetClass, BiConsumer<Player, Packet> onReceive) {
    if(isRegistered(channel)) return;
    
    super.register(channel, packetClass, onReceive);
    Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channel, this::receive);
  }
  
  protected void receive(String channel, Player player, byte[] packet) {
    receive(player, channel, packet);
  }
}
