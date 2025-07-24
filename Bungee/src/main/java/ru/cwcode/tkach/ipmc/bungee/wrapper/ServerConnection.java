package ru.cwcode.tkach.ipmc.bungee.wrapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

@Getter
@RequiredArgsConstructor
public final class ServerConnection {
  private final Server server;
  private final ProxiedPlayer player;
}
