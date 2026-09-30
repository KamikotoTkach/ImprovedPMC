package ru.cwcode.ipmcprobe.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import ru.cwcode.ipmcprobe.ProbeCore;
import ru.cwcode.tkach.ipmc.velocity.IPMC;

import java.lang.reflect.Proxy;

@Plugin(id = "ipmcprobe", name = "IpmcProbe", version = "1", dependencies = @Dependency(id = "ipmc"))
public class IpmcProbeVelocity {
  @Inject
  private ProxyServer server;

  @Subscribe
  public void onInit(ProxyInitializeEvent event) {
    server.getCommandManager().register(server.getCommandManager().metaBuilder("ipmcprobe").build(), (SimpleCommand) invocation -> {
      int[] score = ProbeCore.run(IPMC.packetManager(), loopback(), System.out::println);
      System.out.println("PROBE summary " + score[0] + "/" + score[1]);
    });
  }

  private ServerConnection loopback() {
    ServerConnection[] self = new ServerConnection[1];
    self[0] = (ServerConnection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ServerConnection.class}, (proxy, method, args) -> {
      switch (method.getName()) {
        case "sendPluginMessage" -> {
          server.getEventManager().fire(new PluginMessageEvent(self[0], self[0], (ChannelIdentifier) args[0], (byte[]) args[1])).join();
          return true;
        }
        case "hashCode" -> {
          return 1;
        }
        case "equals" -> {
          return proxy == args[0];
        }
        case "toString" -> {
          return "loopback-connection";
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
