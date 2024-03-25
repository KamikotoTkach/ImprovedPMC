package ru.cwcode.tkach.ipmc.paper;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.tkach.ipmc.PacketManager;

public class IPMC extends JavaPlugin {
  public static PacketManager<Player, JavaPlugin> packetManager;
  
  public static PacketManager<Player, JavaPlugin> packetManager() {
    return packetManager;
  }
  
  @Override
  public void onEnable() {
    super.onEnable();
    
    packetManager = new PacketManager<>(new PaperIncomingPacketHandler(this),
                                        new PaperOutgoingPacketHandler(this));
    
  }
}
