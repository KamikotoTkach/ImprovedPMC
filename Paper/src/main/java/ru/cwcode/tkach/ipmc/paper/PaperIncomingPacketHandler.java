package ru.cwcode.tkach.ipmc.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketUtils;

public class PaperIncomingPacketHandler extends IncomingPacketHandler<Player, Packet, JavaPlugin> {
  public PaperIncomingPacketHandler(JavaPlugin plugin) {
    super(plugin);
    
    Bukkit.getMessenger().registerIncomingPluginChannel(plugin, PacketUtils.INTERNAL_CHANNEL, this::receive);
  }
  
  protected void receive(String channel, Player player, byte[] packet) {
    String subchannel = PacketUtils.extractChannel(packet);
    if (subchannel == null) return;
    
    receive(player, subchannel, packet);
  }
}
