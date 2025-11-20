package ru.cwcode.tkach.ipmc.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketUtils;

public class PaperOutgoingPacketHandler extends OutgoingPacketHandler<Player, Packet, JavaPlugin> {
  public PaperOutgoingPacketHandler(JavaPlugin source) {
    super(source);
    
    Bukkit.getMessenger().registerOutgoingPluginChannel(source, PacketUtils.INTERNAL_CHANNEL);
  }
  
  @Override
  public void send(Packet packet, Player connection) {
    connection.sendPluginMessage(source, PacketUtils.INTERNAL_CHANNEL, packet.write());
  }
}
