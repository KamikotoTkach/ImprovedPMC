package ru.cwcode.tkach.ipmc.paper;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.tkach.ipmc.Packet;
import ru.cwcode.tkach.ipmc.PacketManager;

public class IPMC extends JavaPlugin {
  public static PacketManager<Player, Packet, JavaPlugin, JavaPlugin, PaperIncomingPacketHandler, PaperOutgoingPacketHandler> packetManager;
  public static PacketManager<Player, Packet, JavaPlugin, JavaPlugin, PaperIncomingPacketHandler, PaperOutgoingPacketHandler> clientPacketManager;
  
  public static PacketManager<Player, Packet, JavaPlugin, JavaPlugin, PaperIncomingPacketHandler, PaperOutgoingPacketHandler> packetManager() {
    return packetManager;
  }
  
  public static PacketManager<Player, Packet, JavaPlugin, JavaPlugin, PaperIncomingPacketHandler, PaperOutgoingPacketHandler> clientPacketManager() {
    return clientPacketManager;
  }
  
  @Override
  public void onEnable() {
    super.onEnable();
    
    packetManager = new PacketManager<>(new PaperIncomingPacketHandler(this),
                                        new PaperOutgoingPacketHandler(this));
    clientPacketManager = new PacketManager<>(new PaperIncomingPacketHandler(this,
                                                                             ru.cwcode.tkach.ipmc.PacketUtils.CLIENT_CHANNEL,
                                                                             ru.cwcode.tkach.ipmc.PacketUtils.DEFAULT_CLIENT_MAX_PACKET_BYTES),
                                              new PaperOutgoingPacketHandler(this, ru.cwcode.tkach.ipmc.PacketUtils.CLIENT_CHANNEL));
    
  }
}
