package ru.cwcode.tkach.ipmc.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;

public class PaperOutgoingPacketHandler extends OutgoingPacketHandler<Player, Packet, JavaPlugin> {
  public PaperOutgoingPacketHandler(JavaPlugin source) {
    super(source);
  }
  
  @Override
  public void register(String channel, Class<? extends Packet> packetClass) {
    super.register(channel, packetClass);
    Bukkit.getMessenger().registerOutgoingPluginChannel(source, channel);
  }
  
  @Override
  public void unregister(String channel) {
    super.unregister(channel);
    Bukkit.getMessenger().unregisterOutgoingPluginChannel(source, channel);
  }
  
  @Override
  public void send(Packet packet, Player connection) {
    connection.sendPluginMessage(source, packet.channel(), packet.asByteArray());
  }
}
