package ru.cwcode.ipmcprobe.paper;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.cwcode.ipmcprobe.ProbeCore;
import ru.cwcode.ipmcprobe.ProbePing;
import ru.cwcode.tkach.ipmc.PacketUtils;
import ru.cwcode.tkach.ipmc.paper.IPMC;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class IpmcProbePaper extends JavaPlugin {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    int[] score = ProbeCore.run(IPMC.packetManager(), loopback(), System.out::println);

    List<ProbePing> received = new CopyOnWriteArrayList<>();
    IPMC.clientPacketManager().registerIncomingPacket(ProbePing.class, (player, packet) -> received.add(packet));

    // the client channel drops anything above DEFAULT_CLIENT_MAX_PACKET_BYTES before parsing
    ProbeCore.check(System.out::println, score, "client channel accepts a small packet", () -> {
      IPMC.clientPacketManager().send(new ProbePing("small", 1, 100), loopback());
      return received.size() == 1;
    });
    ProbeCore.check(System.out::println, score, "client channel drops an oversized packet", () -> {
      IPMC.clientPacketManager().send(new ProbePing("big", 2, PacketUtils.DEFAULT_CLIENT_MAX_PACKET_BYTES + 100), loopback());
      return received.size() == 1;
    });

    System.out.println("PROBE summary " + score[0] + "/" + score[1]);
    return true;
  }

  private Player loopback() {
    Player[] self = new Player[1];
    self[0] = (Player) Proxy.newProxyInstance(getClassLoader(), new Class<?>[]{Player.class}, (proxy, method, args) -> {
      switch (method.getName()) {
        case "sendPluginMessage" -> {
          Bukkit.getMessenger().dispatchIncomingMessage(self[0], (String) args[1], (byte[]) args[2]);
          return null;
        }
        case "getUniqueId" -> {
          return new UUID(0, 1);
        }
        case "getName" -> {
          return "loopback";
        }
        case "hashCode" -> {
          return 1;
        }
        case "equals" -> {
          return proxy == args[0];
        }
        case "toString" -> {
          return "loopback-player";
        }
      }
      Class<?> type = method.getReturnType();
      if (type == boolean.class) return false;
      if (type.isPrimitive() && type != void.class) return 0;
      return null;
    });
    return self[0];
  }
}
