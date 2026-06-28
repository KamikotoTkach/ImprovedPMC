package ru.cwcode.tkach.ipmc.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketUtils;

public class PaperOutgoingPacketHandler extends OutgoingPacketHandler<Player, Packet, JavaPlugin> {
  private final String minecraftChannel;
  
  public PaperOutgoingPacketHandler(JavaPlugin source) {
    this(source, PacketUtils.INTERNAL_CHANNEL);
  }
  
  public PaperOutgoingPacketHandler(JavaPlugin source, String minecraftChannel) {
    super(source);
    this.minecraftChannel = minecraftChannel;
    
    Bukkit.getMessenger().registerOutgoingPluginChannel(source, minecraftChannel);
  }
  
  @Override
  public void send(Packet packet, Player connection) {
    connection.sendPluginMessage(source, minecraftChannel, packet.write());
  }
}
