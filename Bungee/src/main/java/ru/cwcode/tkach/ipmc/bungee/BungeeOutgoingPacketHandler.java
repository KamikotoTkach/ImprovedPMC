package ru.cwcode.tkach.ipmc.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.cwcode.tkach.ipmc.OutgoingPacketHandler;
import ru.cwcode.tkach.ipmc.Packet;

public class BungeeOutgoingPacketHandler extends OutgoingPacketHandler<ProxiedPlayer, Packet, IPMC> {
  
  public BungeeOutgoingPacketHandler(IPMC source) {
    super(source);
  }
  
  @Override
  public void send(Packet packet, ProxiedPlayer proxiedPlayer) {
    proxiedPlayer.getServer().getInfo().sendData(packet.channel(), packet.asByteArray());
  }
}
