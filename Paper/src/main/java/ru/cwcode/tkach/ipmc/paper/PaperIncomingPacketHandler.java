package ru.cwcode.tkach.ipmc.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.tkach.ipmc.IncomingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketOptions;
import ru.cwcode.tkach.ipmc.PacketUtils;

public class PaperIncomingPacketHandler extends IncomingPacketHandler<Player, Packet, JavaPlugin> {
  private final int maxPacketBytes;
  
  public PaperIncomingPacketHandler(JavaPlugin plugin) {
    this(plugin, PacketUtils.INTERNAL_CHANNEL);
  }
  
  public PaperIncomingPacketHandler(JavaPlugin plugin, String minecraftChannel) {
    this(plugin, minecraftChannel, PacketOptions.UNLIMITED_BYTES);
  }
  
  public PaperIncomingPacketHandler(JavaPlugin plugin, String minecraftChannel, int maxPacketBytes) {
    super(plugin);
    this.maxPacketBytes = maxPacketBytes;
    
    Bukkit.getMessenger().registerIncomingPluginChannel(plugin, minecraftChannel, this::receive);
  }
  
  protected void receive(String channel, Player player, byte[] packet) {
    if (maxPacketBytes > PacketOptions.UNLIMITED_BYTES && packet.length > maxPacketBytes) return;
    
    String subchannel = PacketUtils.extractChannel(packet);
    if (subchannel == null) return;
    
    receive(player, subchannel, packet);
  }
}
